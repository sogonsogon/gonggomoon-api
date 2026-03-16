package com.sogonsogon.gonggomoon.domain.ai.dto.response;


import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.strategy.domain.GenerateStatus;

public record AiFunctionStatusResponse(
    AiFunctions type,
    Long id,
    GenerateStatus status,
    String error
) {
}
