package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.ai.domain.ExperienceItem;
import java.util.List;

public record ExperienceExtractionSearchResult(
    int totalCount,
    List<ExperienceItem> contents
) {

    public static ExperienceExtractionSearchResult of(List<ExperienceItem> experiences) {
        return new ExperienceExtractionSearchResult(
            experiences.size(),
            experiences
        );
    }
}
