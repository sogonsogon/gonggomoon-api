package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiJobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record BaseCallbackRequest(
    @NotBlank(message = "type은 필수입니다.")
    String type,

    @NotNull(message = "id는 필수입니다.")
    Long id,

    @JsonProperty("user_id")
    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @NotNull(message = "status는 필수입니다.")
    AiJobStatus status,

    // 실패(FAILED) 콜백은 result 없이 error만 올 수 있으므로 null을 허용한다.
    // 성공 콜백의 result 검증은 각 핸들러에서 수행한다.
    JsonNode result,

    String error,

    @JsonProperty("attempt_count")
    Integer attemptCount,

    @JsonProperty("processed_at")
    @NotNull(message = "processedAt은 필수입니다.")
    Instant processedAt
) {
}
