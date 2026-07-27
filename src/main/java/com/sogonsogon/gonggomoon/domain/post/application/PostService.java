package com.sogonsogon.gonggomoon.domain.post.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.application.FileAssetService;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.PortfolioStrategyService;
import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.dto.request.PostAnalysisRequest;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostAnalysisResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.TavilyExtractResponse;
import com.sogonsogon.gonggomoon.domain.post.error.PostErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.post.InMemoryMultipartFile;
import com.sogonsogon.gonggomoon.global.post.TavilyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostAnalysisRepository postAnalysisRepository;
    private final TavilyClient tavilyClient;
    private final AiService aiService;
    private final FileAssetService fileAssetService;
    private final AiUsagePolicyService aiUsagePolicyService;
    private final PortfolioStrategyService portfolioStrategyService;

    @Value("${feature.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    public PostService(PostRepository postRepository,
                       PostAnalysisRepository postAnalysisRepository,
                       TavilyClient tavilyClient,
                       AiService aiService,
                       FileAssetService fileAssetService,
                       AiUsagePolicyService aiUsagePolicyService,
                       PortfolioStrategyService portfolioStrategyService) {
        this.postRepository = postRepository;
        this.postAnalysisRepository = postAnalysisRepository;
        this.tavilyClient = tavilyClient;
        this.aiService = aiService;
        this.fileAssetService = fileAssetService;
        this.aiUsagePolicyService = aiUsagePolicyService;
        this.portfolioStrategyService = portfolioStrategyService;
    }

    public PostAnalysisResponse startPostAnalysis(PostAnalysisRequest request, Long userId) {

//        if (weeklyLimitEnabled && !aiUsagePolicyService.reserve(userId, AiUsageType.POST_ANALYSIS)) {
//            throw new BaseException(PostErrorCode.WEEKLY_LIMIT_EXCEEDED);
//        }

        // 2. 캐시 확인 - 이미 분석된 URL이면 AI 호출 없이 즉시 반환
        Optional<PostAnalysis> cached = postAnalysisRepository.findByUrl(request.postUrl());
        if (cached.isPresent()) {
            Post cachedPost = Post.createFromCache(request.postUrl(), userId, cached.get().getId());
            postRepository.save(cachedPost);

            portfolioStrategyService.createDraft(userId, cachedPost.getId(), cached.get().getId());

            return PostAnalysisResponse.from(cachedPost);
        }

        String rawContent;

        //TODO 77-96 주간 제한 추가 시 try-catch 문 2개 추가 해야함
        TavilyExtractResponse response = tavilyClient.extract(request.postUrl());
        List<TavilyExtractResponse.Result> results = response.results();

        //TODO 실패시 처리 로직
        if (results == null || results.isEmpty()){
            //TODO 에러 코드 확인
            log.warn("Tavily 추출 실패 url={}, failedResults={}", request.postUrl(), response.failedResults());
            throw new BaseException(PostErrorCode.EXTRACTION_FAILED);
        }

        rawContent = results.get(0).rawContent();

        Long fileAssetId;

        InMemoryMultipartFile textFile = InMemoryMultipartFile.fromText(
                rawContent, "raw_content_" + UUID.randomUUID() + ".txt"
        );

        UploadFileRequest uploadFileRequest = new UploadFileRequest(DocumentCategory.OTHER);
        fileAssetId = fileAssetService.uploadFile(userId, uploadFileRequest, textFile).fileAssetId();

        Post newPost = Post.create(request.postUrl(), userId, fileAssetId);
        postRepository.save(newPost);

        try {
            aiService.requestPostAnalysis(userId, newPost.getId(), fileAssetId);
        } catch (RuntimeException e) {
            fileAssetService.deleteTemporaryFiles(List.of(fileAssetId));
            throw e;
        }

        return PostAnalysisResponse.from(newPost);
    }

    public PostResponse getAnalysisByPublicId(UUID analysisId) throws JsonProcessingException {

        PostAnalysis postAnalysis = postAnalysisRepository.findByPublicId(analysisId)
                .orElseThrow(() -> new BaseException(PostErrorCode.POST_ANALYSIS_NOT_FOUND));

        return PostResponse.of(postAnalysis);
    }

    /**
     * 공고를 삭제합니다.
     * 주의: 이 메서드는 호출자(Service)가 소유자 검증을 완료했다는 것을 전제로 합니다.
     */
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);
    }

//    private void refundUsage(Long userId) {
//        if (weeklyLimitEnabled) {
//            aiUsagePolicyService.refund(userId, AiUsageType.POST_ANALYSIS);
//        }
//    }
}
