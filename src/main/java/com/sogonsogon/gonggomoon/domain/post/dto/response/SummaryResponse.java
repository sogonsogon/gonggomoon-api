package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;

public record SummaryResponse(
        Long id,
        String url,
        PostStatus status,
        String title,
        String refinedContent
) {
}
