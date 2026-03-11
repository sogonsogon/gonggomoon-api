package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;

import java.time.Instant;

public record PostsResponse(
        Long postId,
        Long companyId,
        String companyName,
        String platformName,
        String postTitle,
        Integer experienceLevel,
        JobType jobType,
        Instant stateDate,
        Instant dueDate
) {
}
