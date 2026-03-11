package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;

import java.time.Instant;

public record PostResponse(
        Long companyId,
        Long industryId,
        String companyName,
        String industryName,
        String postTitle,
        Integer experienceLevel,
        String originalContent,
        JobType jobType,
        PostStatus status,
        Instant stateDate,
        Instant dueDate
) {
}
