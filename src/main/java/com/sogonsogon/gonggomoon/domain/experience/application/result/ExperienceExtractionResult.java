package com.sogonsogon.gonggomoon.domain.experience.application.result;

import lombok.Builder;

@Builder
public record ExperienceExtractionResult(
        Long extractionId
) {
    public static ExperienceExtractionResult from(Long extractionId) {
        return ExperienceExtractionResult.builder()
                .extractionId(extractionId)
                .build();
    }
}
