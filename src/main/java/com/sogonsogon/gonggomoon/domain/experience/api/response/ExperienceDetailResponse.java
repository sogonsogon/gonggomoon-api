package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceDetailResult;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ExperienceDetailResponse(
        Long experienceId,
        String title,
        String experienceType,
        String experienceContent,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ExperienceDetailResponse from (ExperienceDetailResult result) {
        return ExperienceDetailResponse.builder()
                .experienceId(result.experienceId())
                .title(result.title())
                .experienceType(result.experienceType())
                .experienceContent(result.experienceContent())
                .startDate(result.startDate())
                .endDate(result.endDate())
                .build();
    }
}
