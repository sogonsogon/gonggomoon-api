package com.sogonsogon.gonggomoon.domain.experience.application.result;

import lombok.Builder;

@Builder
public record ExperienceExtractionResult(
        Long extractedExperienceId
) {
    public static ExperienceExtractionResult from (Long extractedExperienceId) {
        return ExperienceExtractionResult.builder()
                .extractedExperienceId(extractedExperienceId)
                .build();
    }
}
