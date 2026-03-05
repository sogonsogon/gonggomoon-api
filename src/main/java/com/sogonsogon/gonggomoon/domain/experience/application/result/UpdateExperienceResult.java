package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateExperienceResult(
        Long experienceId,
        String title,
        String experienceType,
        String experienceContent,
        LocalDate startDate,
        LocalDate endDate
) {
    public static UpdateExperienceResult from (Experience experience) {
        return UpdateExperienceResult.builder()
                .experienceId(experience.getId())
                .title(experience.getTitle())
                .experienceType(String.valueOf(experience.getExperienceType()))
                .experienceContent(experience.getExperienceContent())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .build();
    }
}
