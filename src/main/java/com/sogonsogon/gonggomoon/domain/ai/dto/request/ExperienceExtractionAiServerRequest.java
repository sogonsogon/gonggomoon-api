package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExperienceExtractionAiServerRequest(
    @JsonProperty("extracted_experience_id")
    Long extractedExperienceId
) {
}
