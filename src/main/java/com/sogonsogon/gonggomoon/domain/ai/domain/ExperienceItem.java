package com.sogonsogon.gonggomoon.domain.ai.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExperienceItem {

    private String title;
    private String experienceContent;
    private ExperienceType experienceType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth endDate;

    @Builder
    public ExperienceItem(
        String title,
        String experienceContent,
        ExperienceType experienceType,
        YearMonth startDate,
        YearMonth endDate
    ) {
        this.title = title;
        this.experienceContent = experienceContent;
        this.experienceType = experienceType;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
