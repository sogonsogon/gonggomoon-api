package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.Builder;

import java.time.LocalDate;
@Builder
public record ExperienceDetailResult(
        Long experienceId,
        String title,
        String experienceType,
        String experienceContent,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ExperienceDetailResult from (Experience experience) {
        return ExperienceDetailResult.builder()
                .experienceId(experience.getId())
                .title(experience.getTitle())
                .experienceType(String.valueOf(experience.getExperienceType()))
                .experienceContent(experience.getExperienceContent())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .build();
    }
}
