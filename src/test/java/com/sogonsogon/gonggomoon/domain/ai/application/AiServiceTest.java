package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    private PostRepository postRepository;

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

    @Test
    void subscribeSendsStrategyForTheRequestedPostAndCompletesEmitter() {
        Long userId = 1L;
        Long postId = 10L;
        Long strategyId = 200L;
        Post completedPost = Post.createFromCache("https://example.com/job", userId, 100L);
        org.springframework.test.util.ReflectionTestUtils.setField(completedPost, "id", postId);
        UUID postPublicId = completedPost.getPublicId();
        AiFunctionStatusRequest request =
            new AiFunctionStatusRequest(AiFunctions.POST_ANALYSIS, postPublicId);
        SseEmitter emitter = new SseEmitter();
        PortfolioStrategy strategy = PortfolioStrategy.builder()
            .id(strategyId)
            .userId(userId)
            .postId(postId)
            .postAnalysisId(100L)
            .status(PortfolioStrategyGenerateStatus.DRAFT)
            .build();
        AiFunctionStatusResponse response = new AiFunctionStatusResponse(
            AiFunctions.POST_ANALYSIS,
            postPublicId,
            AiFunctionStatus.READY,
            strategy.getPublicId(),
            null
        );

        when(aiJobSseService.register(userId, AiFunctions.POST_ANALYSIS, postPublicId))
            .thenReturn(emitter);
        when(postRepository.findByPublicIdAndCreatedBy(postPublicId, userId))
            .thenReturn(Optional.of(completedPost));
        when(portfolioStrategyRepository.findByPostIdAndUserId(postId, userId))
            .thenReturn(Optional.of(strategy));

        SseEmitter result = aiService.subscribe(userId, request);

        assertThat(result).isSameAs(emitter);
        verify(aiJobSseService).send(userId, response);
        verify(aiJobSseService).complete(userId, AiFunctions.POST_ANALYSIS, postPublicId);
        verify(portfolioStrategyRepository).findByPostIdAndUserId(postId, userId);
    }
}
