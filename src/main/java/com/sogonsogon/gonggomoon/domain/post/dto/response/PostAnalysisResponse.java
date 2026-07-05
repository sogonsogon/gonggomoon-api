package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;

public record PostAnalysisResponse(
        Long postId,
        String url,
        PostStatus status
) {
    public static PostAnalysisResponse from(Post post) {
        return new PostAnalysisResponse(post.getId(), post.getUrl(), post.getStatus());
    }
}
