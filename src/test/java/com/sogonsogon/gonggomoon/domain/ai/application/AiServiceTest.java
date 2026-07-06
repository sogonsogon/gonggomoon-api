package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private ExtractedExperienceRepository extractedExperienceRepository;

    @Mock
    private PortfolioStrategyRepository portfolioStrategyRepository;

    @Mock
    private InterviewStrategyRepository interviewStrategyRepository;

    @Mock
    private AiServerClient aiServerClient;

    @Mock
    private AiJobSseService aiJobSseService;

    @InjectMocks
    private AiService aiService;

    @Test
    void requestPortfolioStrategyGeneration_usesDefaultPositionAndIndustry() {
        Long userId = 1L;
        Long portfolioStrategyId = 100L;
        Long postAnalysisId = 10L;
        List<Experience> experiences = List.of();

        aiService.requestPortfolioStrategyGeneration(userId, portfolioStrategyId, experiences, postAnalysisId);

        ArgumentCaptor<PortfolioStrategyRequest> captor = ArgumentCaptor.forClass(PortfolioStrategyRequest.class);
        verify(aiServerClient).requestPortfolioStrategyGeneration(captor.capture());

        PortfolioStrategyRequest request = captor.getValue();
        assertEquals(userId, request.userId());
        assertEquals(portfolioStrategyId, request.portfolioStrategyId());
        assertEquals(postAnalysisId, request.postAnalysisId());
        assertEquals(experiences, request.experiences());
        assertNull(request.positionType());
        assertEquals("마스터", request.industryType());
    }
}
