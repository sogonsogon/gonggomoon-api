package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.error.PostAnalysisErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private PostAnalysisRepository postAnalysisRepository;

    @InjectMocks
    private AiService aiService;

    @Test
    void requestPortfolioStrategyGeneration_usesDefaultPositionAndIndustry() {
        Long userId = 1L;
        Long portfolioStrategyId = 100L;
        Long postAnalysisId = 10L;
        List<Experience> experiences = List.of();
        PostAnalysis postAnalysis = PostAnalysis.create("https://example.com/jobs/1", "백엔드 채용", "3년차 백엔드 개발자");

        when(postAnalysisRepository.findById(postAnalysisId)).thenReturn(Optional.of(postAnalysis));

        aiService.requestPortfolioStrategyGeneration(userId, portfolioStrategyId, experiences, postAnalysisId);

        ArgumentCaptor<PortfolioStrategyRequest.PostAnalysisInput> captor =
                ArgumentCaptor.forClass(PortfolioStrategyRequest.PostAnalysisInput.class);
        verify(aiServerClient).requestPortfolioStrategyGeneration(
                eq(portfolioStrategyId), eq(userId), eq(experiences), eq("마스터"), eq("마스터"), captor.capture());

        PortfolioStrategyRequest.PostAnalysisInput postAnalysisInput = captor.getValue();
        assertEquals("백엔드 채용", postAnalysisInput.title());
        assertEquals("3년차 백엔드 개발자", postAnalysisInput.summary());
    }

    @Test
    void requestPortfolioStrategyGeneration_fail_whenPostAnalysisNotFound() {
        when(postAnalysisRepository.findById(anyLong())).thenReturn(Optional.empty());

        BaseException exception = assertThrows(
                BaseException.class,
                () -> aiService.requestPortfolioStrategyGeneration(1L, 100L, List.of(), 10L)
        );

        assertEquals(PostAnalysisErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(aiServerClient, org.mockito.Mockito.never()).requestPortfolioStrategyGeneration(
                anyLong(), anyLong(), anyList(), anyString(), anyString(), any());
    }
}
