package com.sogonsogon.gonggomoon.domain.experience.application.result;

import lombok.Builder;
import java.util.List;

@Builder
public record ExperienceExtractionResult(
        List<Long> extractedExperienceIds
) {
    public static ExperienceExtractionResult from (List<Long> extractedExperienceIds) {
        return ExperienceExtractionResult.builder()
                .extractedExperienceIds(extractedExperienceIds)
                .build();
    }
}
