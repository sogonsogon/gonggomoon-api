package com.sogonsogon.gonggomoon.domain.bookmark.dto;

import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;

import java.time.Instant;

public record BookmarkListResponse(
        Long bookmarkId,
        Long postId,
        String postTitle,
        String companyName,
        PostStatus postStatus,
        Instant startDate,
        Instant dueDate,
        Instant createdAt
) {
}
