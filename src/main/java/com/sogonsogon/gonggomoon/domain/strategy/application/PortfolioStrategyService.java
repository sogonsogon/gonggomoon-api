package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.PortfolioStrategyContentGenerator;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioStrategyService {

    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final ExperienceRepository experienceRepository;
    private final PortfolioStrategyContentGenerator portfolioStrategyContentGenerator;
    private final ObjectMapper objectMapper;

    /**
     * 포트폴리오 전략 생성 서비스
     */
    public GeneratePortfolioStrategyResult generate(Long userId, GeneratePortfolioStrategyRequest req) {

        if (req.experienceIds() == null || req.experienceIds().isEmpty()) {
            throw new BaseException(PortfolioStrategyErrorCode.EXPERIENCE_IDS_REQUIRED);
        }

        if (req.experienceIds().size() > 2) {
            throw new BaseException(PortfolioStrategyErrorCode.TOO_MANY_EXPERIENCES);
        }

        List<Experience> experiences = experienceRepository.findAllByIdInAndUserId(req.experienceIds(), userId);
        if (experiences.size() != req.experienceIds().size()) {
            throw new BaseException(PortfolioStrategyErrorCode.REQUESTED_EXPERIENCE_NOT_FOUND);
        }

        // 전략 생성
        PortfolioStrategyContent content = portfolioStrategyContentGenerator.generate(experiences, req);

        // 전략 결과 JSON 반환
        String resultJson;
        try {
            resultJson = objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_SERIALIZATION_FAILED);
        }

        // 전략 엔티티 생성
        PortfolioStrategy strategy = PortfolioStrategy.create(
                userId,
                req.jobType(),
                req.industryType(),
                resultJson);

        portfolioStrategyRepository.save(strategy);

        return GeneratePortfolioStrategyResult.of(strategy.getId());
    }
}
