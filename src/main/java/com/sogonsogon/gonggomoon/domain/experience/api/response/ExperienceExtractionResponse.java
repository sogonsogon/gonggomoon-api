package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import lombok.Builder;

@Builder
public record ExperienceExtractionResponse(
        Long extractionId
) {
    public static ExperienceExtractionResponse from(ExperienceExtractionResult result) {
        return ExperienceExtractionResponse.builder()
                .extractionId(result.extractionId())
                .build();
    }
}
