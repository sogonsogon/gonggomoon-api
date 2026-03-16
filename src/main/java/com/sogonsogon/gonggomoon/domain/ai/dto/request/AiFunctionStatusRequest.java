package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import jakarta.validation.constraints.NotNull;

public record AiFunctionStatusRequest(
    @NotNull(message = "type은 null 일 수 없습니다.")
    AiFunctions type,

    @NotNull(message = "id는 null 일 수 없습니다.")
    Long id
) {
}
