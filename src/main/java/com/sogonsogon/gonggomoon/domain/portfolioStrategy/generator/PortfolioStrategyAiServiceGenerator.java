package com.sogonsogon.gonggomoon.domain.portfolioStrategy.generator;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PortfolioStrategyAiServiceGenerator implements PortfolioStrategyContentGenerator{

    private final AiService aiService;

    @Override
    public void request(
            Long userId,
            Long portfolioStrategyId,
            List<Experience> experiences,
            Long postAnalysisId
    ) {
        aiService.requestPortfolioStrategyGeneration(userId, portfolioStrategyId, experiences, postAnalysisId);
    }
}
