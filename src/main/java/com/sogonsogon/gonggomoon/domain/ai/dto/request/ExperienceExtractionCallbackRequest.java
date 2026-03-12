package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public record ExperienceExtractionCallbackRequest(
    String type,
    Long id,
    Long userId,
    String status,
    JsonNode result,
    Instant processedAt
) {
}
