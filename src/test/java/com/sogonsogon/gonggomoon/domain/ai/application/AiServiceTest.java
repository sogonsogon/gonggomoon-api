package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

        PostAnalysis postAnalysis = PostAnalysis.create("https://example.com/job", "공고 제목", "공고 요약");
        when(postAnalysisRepository.findById(postAnalysisId)).thenReturn(Optional.of(postAnalysis));

        aiService.requestPortfolioStrategyGeneration(userId, portfolioStrategyId, experiences, postAnalysisId);

        // 기본값(positionType=null, industryType="마스터")이 채워지고,
        // 공고 분석 결과(title, summary)가 인라인으로 워커에 전달되는지 검증한다.
        verify(aiServerClient).requestPortfolioStrategyGeneration(
            portfolioStrategyId,
            userId,
            List.of(),
            null,
            "마스터",
            new PortfolioStrategyRequest.PostAnalysisInput("공고 제목", "공고 요약"));
    }
}
