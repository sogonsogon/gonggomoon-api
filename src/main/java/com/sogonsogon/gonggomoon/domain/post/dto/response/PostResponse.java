package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;

import java.time.Instant;

public record PostResponse(
        Long postId,
        Long companyId,
        Long industryId,
        String companyName,
        String industryName,
        String postTitle,
        String postUrl,
        Integer experienceLevel,
        String originalContent,
        JsonNode analyzedContent,
        JobType jobType,
        PostStatus status,
        Instant stateDate,
        Instant dueDate
) {
}
