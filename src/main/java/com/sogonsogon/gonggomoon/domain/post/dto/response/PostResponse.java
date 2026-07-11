package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;

public record PostResponse(
        Long postId,
        String url,
        String title,
        String summary
) {
    public static PostResponse of(Long postId, PostAnalysis analysis) {
        return new PostResponse(
                postId,
                analysis.getUrl(),
                analysis.getTitle(),
                analysis.getSummary()
        );
    }
}
