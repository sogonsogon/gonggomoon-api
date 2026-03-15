package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.industry.error.IndustryErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.PortfolioStrategyContentGenerator;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioStrategyService {

    private final AiService aiService;
    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final ExperienceRepository experienceRepository;
    private final IndustryRepository industryRepository;
    private final PortfolioStrategyContentGenerator portfolioStrategyContentGenerator;
    private final ObjectMapper objectMapper;

    @Value("${strategy.portfolio.daily-limit-enabled:true}")
    private boolean dailyLimitEnabled;

    /**
     * 포트폴리오 전략 생성 서비스
     * save 후 예외가 나면 insert도 롤백되게 한다.
     */
    @Transactional
    public GeneratePortfolioStrategyResult generate(Long userId, GeneratePortfolioStrategyRequest req) {

        if (req.experienceIds() == null || req.experienceIds().isEmpty()) {
            throw new BaseException(PortfolioStrategyErrorCode.EXPERIENCE_IDS_REQUIRED);
        }

        /**
         * now : 기준 시각 1개
         * today : 그 시각에서 파생된 날짜
         */
        Instant now = Instant.now();
        LocalDate today = now.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();

        /**
         * 전략이 이미 있는지 검증
         */
        if (dailyLimitEnabled && portfolioStrategyRepository.existsByUserIdAndGeneratedDate(userId, today)) {
            throw new BaseException(PortfolioStrategyErrorCode.ALREADY_CREATED_TODAY);
        }

        List<Experience> experiences = experienceRepository.findAllByIdInAndUserId(req.experienceIds(), userId);
        if (experiences.size() != req.experienceIds().size()) {
            throw new BaseException(PortfolioStrategyErrorCode.REQUESTED_EXPERIENCE_NOT_FOUND);
        }

        // TODO : 여기서부터 세훈 작업
        // NOTE : AI에게 industry name을 넘겨주기 위해서 조회합니다.
        Industry industry = industryRepository.findById(req.industryId()).orElseThrow(
            () -> new BaseException(IndustryErrorCode.INDUSTRY_NOT_FOUND));

        // 전략 엔티티 생성 (result Json이 없는 전략 엔티티를 생성합니다.)
        PortfolioStrategy strategy = PortfolioStrategy.create(
            userId,
            req.jobType(),
            req.industryId(),
            experiences.size(),
            now,
            today
        );

        // NOTE : 포폴 전략을 저장하여 ID값을 받아옵니다.
        PortfolioStrategy newStrategy = portfolioStrategyRepository.save(strategy);

        // AI Service에 포폴 전략 생성 요청을 보냅니다.
        aiService.requestPortfolioStrategyGeneration(
            userId,
            newStrategy.getId(),
            experiences,
            req.jobType().name(),
            industry.getName()
        );

        // TODO : 이전에 주영님이 만들어주신 내용 주석 처리
//        // 전략 생성
//        PortfolioStrategyContent content = portfolioStrategyContentGenerator.generate(experiences, req);
//
//        // 전략 결과 JSON 반환
//        String resultJson;
//        try {
//            resultJson = objectMapper.writeValueAsString(content);
//        } catch (JsonProcessingException e) {
//            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_SERIALIZATION_FAILED);
//        }
//
//        // 전략 엔티티 생성
//        PortfolioStrategy strategy = PortfolioStrategy.create(
//                userId,
//                req.positionType(),
//                req.industryId(),
//                resultJson,
//                experiences.size(),
//                now,
//                today);


        return GeneratePortfolioStrategyResult.of(newStrategy.getId());
    }

    /**
     * 포트폴리오 전략 목록 조회 서비스
     */
    public PortfolioStrategyListResult getPortfolioStrategyList(Long userId) {
        List<PortfolioStrategyListResultItem> items = portfolioStrategyRepository.findPortfolioStrategyListByUserId(userId);

        return PortfolioStrategyListResult.from(items);
    }

    /**
     * 포트폴리오 전략 상세 조회 서비스
     */
    public PortfolioStrategyDetailResult getPortfolioStrategyDetail(Long strategyId, Long userId) {
        PortfolioStrategy portfolioStrategy = portfolioStrategyRepository.findByIdAndUserId(strategyId, userId)
                .orElseThrow(() -> new BaseException(PortfolioStrategyErrorCode.NOT_FOUND));

        PortfolioStrategyContent content;
        try {
            content = objectMapper.readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class);
        } catch (JsonProcessingException e) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_DESERIALIZATION_FAILED);
        }

        Long industryId = portfolioStrategy.getIndustryId();
        String industryName;
        if (industryId == null) {
            industryName = "마스터";
        } else {
            Industry industry = industryRepository.findById(portfolioStrategy.getIndustryId())
                    .orElseThrow(() -> new BaseException(IndustryErrorCode.INDUSTRY_NOT_FOUND));
            industryName = industry.getName();
        }

        return PortfolioStrategyDetailResult.of(portfolioStrategy, content, industryName);
    }

    /**
     * 포트폴리오 삭제 서비스
     */
    public void deletePortfolioStrategy(Long strategyId, Long userId) {
        PortfolioStrategy portfolioStrategy = portfolioStrategyRepository.findByIdAndUserId(strategyId, userId)
                .orElseThrow(() -> new BaseException(PortfolioStrategyErrorCode.NOT_FOUND));

        portfolioStrategyRepository.delete(portfolioStrategy);
    }
}
