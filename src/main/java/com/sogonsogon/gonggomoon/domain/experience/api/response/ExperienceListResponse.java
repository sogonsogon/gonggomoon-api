package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceListResult;
import lombok.Builder;

import java.util.List;

@Builder
public record ExperienceListResponse(
        int totalCount,
        List<ExperienceListResultItem> contents
) {
    public static ExperienceListResponse from (ExperienceListResult result) {
        return ExperienceListResponse.builder()
                .totalCount(result.totalCount())
                .contents(result.contents())
                .build();
    }
}
