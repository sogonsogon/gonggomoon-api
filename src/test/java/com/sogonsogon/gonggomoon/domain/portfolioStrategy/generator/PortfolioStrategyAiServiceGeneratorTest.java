package com.sogonsogon.gonggomoon.domain.portfolioStrategy.generator;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PortfolioStrategyAiServiceGeneratorTest {

    @Mock
    private AiService aiService;

    @InjectMocks
    private PortfolioStrategyAiServiceGenerator generator;

    @Test
    void request_passesPostAnalysisIdToAiService() {
        Long userId = 1L;
        Long portfolioStrategyId = 100L;
        Long postAnalysisId = 10L;
        List<Experience> experiences = List.of();

        generator.request(userId, portfolioStrategyId, experiences, postAnalysisId);

        verify(aiService).requestPortfolioStrategyGeneration(
                userId,
                portfolioStrategyId,
                experiences,
                postAnalysisId
        );
    }
}
