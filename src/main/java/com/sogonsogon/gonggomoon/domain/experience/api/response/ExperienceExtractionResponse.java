package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import lombok.Builder;
import java.util.UUID;

@Builder
public record ExperienceExtractionResponse(
        UUID extractionId
) {
    public static ExperienceExtractionResponse from(ExperienceExtractionResult result) {
        return ExperienceExtractionResponse.builder()
                .extractionId(result.extractionId())
                .build();
    }
}
