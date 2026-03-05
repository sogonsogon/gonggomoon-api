package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.UpsertExperienceResult;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpsertExperienceResponse(
        Long experienceId,
        String title,
        String experienceType,
        String experienceContent,
        LocalDate startDate,
        LocalDate endDate
) {
    public static UpsertExperienceResponse from (UpsertExperienceResult result) {
        return UpsertExperienceResponse.builder()
                .experienceId(result.experienceId())
                .title(result.title())
                .experienceType(result.experienceType())
                .experienceContent(result.experienceContent())
                .startDate(result.startDate())
                .endDate(result.endDate())
                .build();
    }
}
