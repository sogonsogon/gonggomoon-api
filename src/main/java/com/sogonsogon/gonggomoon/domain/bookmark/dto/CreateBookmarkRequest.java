package com.sogonsogon.gonggomoon.domain.bookmark.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBookmarkRequest(
        @NotNull(message = "공고에 대한 정보가 필요합니다.")
        Long postId
) {
}
