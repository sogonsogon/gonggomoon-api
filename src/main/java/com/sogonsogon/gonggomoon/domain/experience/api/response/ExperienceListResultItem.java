package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.Builder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Experience list 객체입니다.
 */
@Builder
public record ExperienceListResultItem(
        Long experienceId,
        String title,
        String experienceType,
        String startDate,
        String endDate
) {
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    public static ExperienceListResultItem from(Experience e) {
        return ExperienceListResultItem.builder()
                .experienceId(e.getId())
                .title(e.getTitle())
                .experienceType(e.getExperienceType().name())
                .startDate(formatYm(e.getStartDate()))
                .endDate(formatYm(e.getEndDate()))
                .build();
    }

    private static String formatYm(LocalDate d) {
        return d == null ? null : d.format(YM);
    }
}
