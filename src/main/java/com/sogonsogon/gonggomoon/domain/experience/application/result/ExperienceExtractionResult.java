package com.sogonsogon.gonggomoon.domain.experience.application.result;

import lombok.Builder;
import java.util.UUID;

@Builder
public record ExperienceExtractionResult(
        UUID extractionId
) {
    public static ExperienceExtractionResult from(UUID extractionId) {
        return ExperienceExtractionResult.builder()
                .extractionId(extractionId)
                .build();
    }
}
