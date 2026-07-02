package com.sogonsogon.gonggomoon.domain.post.application;

import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.post.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.dto.request.SummaryRequest;
import com.sogonsogon.gonggomoon.domain.post.dto.response.SummaryResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.TavilyExtractResponse;
import com.sogonsogon.gonggomoon.global.post.TavilyClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostAnalysisRepository postAnalysisRepository;
    private final TavilyClient tavilyClient;

    public PostService(PostRepository postRepository,
                       PostAnalysisRepository postAnalysisRepository,
                       TavilyClient tavilyClient) {
        this.postRepository = postRepository;
        this.postAnalysisRepository = postAnalysisRepository;
        this.tavilyClient = tavilyClient;
    }

    @Transactional
    public SummaryResponse extractAndRefinedPost(SummaryRequest request, Long userId) {

        Post newPost = Post.create(request.postUrl(), userId);
        postRepository.save(newPost);

        Optional<PostAnalysis> cached = postAnalysisRepository.findByUrl(request.postUrl());
        if (cached.isPresent()) {
            PostAnalysis analysis = cached.get();
            newPost.success(analysis.getId());
            return new SummaryResponse(
                    newPost.getId(),
                    newPost.getUrl(),
                    newPost.getStatus(),
                    analysis.getTitle(),
                    analysis.getSummary()
            );
        }

        try {
            TavilyExtractResponse response = tavilyClient.extract(request.postUrl());
            List<TavilyExtractResponse.Result> results = response.results();

            //TODO 실패시 처리 로직
            if (results == null || results.isEmpty()){
                newPost.failed();
                return new SummaryResponse(newPost.getId(), newPost.getUrl(), newPost.getStatus(), null, null);
            }

            String rawContent = results.get(0).rawContent();

            //TODO 이후 LLM 이용 데이터 정리 로직
            String title = null;
            String refinedContent = null;

            PostAnalysis postAnalysis = PostAnalysis.create(request.postUrl(), title, refinedContent);
            postAnalysisRepository.save(postAnalysis);

            newPost.success(postAnalysis.getId());

            return new SummaryResponse(newPost.getId(), newPost.getUrl(), newPost.getStatus(), title, refinedContent);

        } catch (Exception e) {
            newPost.failed();
            return new SummaryResponse(newPost.getId(), newPost.getUrl(), newPost.getStatus(), null, null);
        }
    }
}
