package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.industry.error.IndustryErrorCode;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.generator.PortfolioStrategyContentGenerator;
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

    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final ExperienceRepository experienceRepository;
    private final IndustryRepository industryRepository;
    private final PortfolioStrategyContentGenerator portfolioStrategyContentGenerator;
    private final ObjectMapper objectMapper;

    private final AiService aiService;

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

        /**
         * 경험 목록 조회
         */
        List<Experience> experiences = experienceRepository.findAllByIdInAndUserId(req.experienceIds(), userId);
        if (experiences.size() != req.experienceIds().size()) {
            throw new BaseException(PortfolioStrategyErrorCode.REQUESTED_EXPERIENCE_NOT_FOUND);
        }

        /**
         * 포트폴리오 전략 엔티티 생성
         * 기본 값으로 생성하는 이유는 AI가 포트폴리오 전략을 생성하고 난 후,
         * 어떤 portfolio_strategy에 그 값을 저장해야하는지 명시
         */
        PortfolioStrategy strategy = PortfolioStrategy.create(
                userId,
                req.jobType(),
                req.industryId(),
                experiences.size(),
                now,
                today);

        PortfolioStrategy draftStrategy = portfolioStrategyRepository.save(strategy);

        /**
         * 산업 조회 및 산업이름 반환
         * 산업을 조회하는 이유는 AI에게 포트폴리오 전략을 생성할 때, 값으로 넣어주기 위함
         */
        String industryName = resolveIndustryName(draftStrategy, req.industryId());

        // AI Service에 포폴 전략 생성 요청
        portfolioStrategyContentGenerator.request(
                userId,
                draftStrategy.getId(),
                experiences,
                req.jobType().name(),
                industryName);

        return GeneratePortfolioStrategyResult.from(draftStrategy.getId());
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

        if (portfolioStrategy.getStatus() == PortfolioStrategyGenerateStatus.PROCESSING) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_NOT_READY);
        }

        if (portfolioStrategy.getStatus() == PortfolioStrategyGenerateStatus.FAILED) {
            throw new BaseException(PortfolioStrategyErrorCode.GENERATION_FAILED);
        }

        String resultJson = portfolioStrategy.getResultJson();
        if (resultJson == null || resultJson.isBlank()) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_EMPTY);
        }

        PortfolioStrategyContent content;
        try {
            content = objectMapper.readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class);
        } catch (JsonProcessingException e) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_DESERIALIZATION_FAILED);
        }

        Long industryId = portfolioStrategy.getIndustryId();

        String industryName = resolveIndustryName(portfolioStrategy, industryId);

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

    public String resolveIndustryName(PortfolioStrategy portfolioStrategy, Long industryId) {
        String industryName;
        if (industryId == null) {
            industryName = "마스터";
        } else {
            Industry industry = industryRepository.findById(portfolioStrategy.getIndustryId())
                    .orElseThrow(() -> new BaseException(IndustryErrorCode.INDUSTRY_NOT_FOUND));
            industryName = industry.getName();
        }
        return industryName;
    }
}
