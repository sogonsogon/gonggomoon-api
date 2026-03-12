package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ExperienceExtractionCallbackRequest(
    @NotBlank(message = "type은 필수입니다.")
    String type,

    @NotNull(message = "id는 필수입니다.")
    Long id,

    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @NotBlank(message = "status는 필수입니다.")
    String status,

    @NotNull(message = "result는 필수입니다.")
    JsonNode result,

    @NotNull(message = "processedAt은 필수입니다.")
    Instant processedAt
) {
}
