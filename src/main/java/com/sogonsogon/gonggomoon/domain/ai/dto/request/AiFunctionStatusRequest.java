package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AiFunctionStatusRequest(
    @Schema(description = "AI 작업 종류", example = "PORTFOLIO_STRATEGY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "type은 null 일 수 없습니다.")
    AiFunctions type,

    @Schema(description = "작업 대상 ID (전략 ID, 추출 ID 등 type에 대응되는 식별자)", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id는 null 일 수 없습니다.")
    Long id
) {
}
