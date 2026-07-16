package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.industry.error.IndustryErrorCode;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailQueryResult;
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

    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${strategy.portfolio.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

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
         * 경험 목록 조회
         */
        List<Experience> experiences = experienceRepository.findAllByIdInAndUserId(req.experienceIds(), userId);
        if (experiences.size() != req.experienceIds().size()) {
            throw new BaseException(PortfolioStrategyErrorCode.REQUESTED_EXPERIENCE_NOT_FOUND);
        }

        PortfolioStrategy draftStrategy = portfolioStrategyRepository
                .findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        req.postAnalysisId(),
                        PortfolioStrategyGenerateStatus.DRAFT
                )
                .orElseThrow(() -> new BaseException(PortfolioStrategyErrorCode.NOT_FOUND));

        /**
         * 이번 주 성공한 전략 생성 횟수를 검증
         */
        if (weeklyLimitEnabled && !aiUsagePolicyService.reserve(userId, AiUsageType.PORTFOLIO_STRATEGY)) {
            throw new BaseException(PortfolioStrategyErrorCode.WEEKLY_LIMIT_EXCEEDED);
        }

        draftStrategy.startProcessing(experiences.size());
        portfolioStrategyRepository.save(draftStrategy);

        // AI Service에 포폴 전략 생성 요청
        portfolioStrategyContentGenerator.request(
                userId,
                draftStrategy.getId(),
                experiences,
                draftStrategy.getPostAnalysisId());

        return GeneratePortfolioStrategyResult.from(draftStrategy.getId());
    }

    @Transactional
    public Long createDraft(Long userId, Long postId, Long postAnalysisId) {
        Instant now = Instant.now();
        LocalDate today = now.atZone(KST).toLocalDate();

        PortfolioStrategy draft = PortfolioStrategy.createDraft(userId, postId, postAnalysisId, now, today);
        PortfolioStrategy savedDraft = portfolioStrategyRepository.save(draft);

        return savedDraft.getId();
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
        PortfolioStrategyDetailQueryResult queryResult = getPortfolioStrategyDetailQueryResult(strategyId, userId);
        PortfolioStrategy portfolioStrategy = queryResult.portfolioStrategy();

        if (portfolioStrategy.getStatus() == PortfolioStrategyGenerateStatus.DRAFT ||
                portfolioStrategy.getStatus() == PortfolioStrategyGenerateStatus.PROCESSING) {
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

        return PortfolioStrategyDetailResult.of(portfolioStrategy, queryResult.postAnalysisTitle(), content, industryName);
    }

    /**
     * 상세 조회에 필요한 포트폴리오 전략과 채용 공고 분석 제목을 함께 조회한다.
     */
    private PortfolioStrategyDetailQueryResult getPortfolioStrategyDetailQueryResult(Long strategyId, Long userId) {
        return portfolioStrategyRepository
                .findPortfolioStrategyDetailByIdAndUserId(strategyId, userId)
                .orElseThrow(() -> new BaseException(PortfolioStrategyErrorCode.NOT_FOUND));
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
