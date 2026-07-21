package com.sogonsogon.gonggomoon.domain.ai.dto.response;


import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import java.util.UUID;

public record AiFunctionStatusResponse(
    AiFunctions type,
    UUID id,
    AiFunctionStatus status,
    UUID strategyId,
    String error
) {
}
