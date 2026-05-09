package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ExperienceExtractionAiServerRequest(
    @JsonProperty("extracted_experience_ids")
    List<Long> extractedExperienceIds
) {
}
