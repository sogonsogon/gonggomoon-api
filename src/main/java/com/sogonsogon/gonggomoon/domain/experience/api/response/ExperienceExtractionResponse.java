package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import lombok.Builder;
import java.util.List;

@Builder
public record ExperienceExtractionResponse(
        List<Long> extractedExperienceIds
) {
    public static ExperienceExtractionResponse from (ExperienceExtractionResult result) {
        return ExperienceExtractionResponse.builder()
                .extractedExperienceIds(result.extractedExperienceIds())
                .build();
    }
}
