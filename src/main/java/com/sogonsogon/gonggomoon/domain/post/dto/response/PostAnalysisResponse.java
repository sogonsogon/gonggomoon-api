package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record PostAnalysisResponse(
        @Schema(description = "생성된 공고 공개 ID. 분석 상태 조회(SSE)에 사용한다.", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID postId,

        @Schema(description = "분석을 요청한 공고 URL", example = "https://careers.example.com/jobs/12345")
        String url,

        @Schema(description = "분석 상태. 이미 분석된 URL이면 캐시로 즉시 SUCCESS, 아니면 PENDING으로 시작한다.", example = "PENDING")
        PostStatus status
) {
    public static PostAnalysisResponse from(Post post) {
        return new PostAnalysisResponse(post.getPublicId(), post.getUrl(), post.getStatus());
    }
}
