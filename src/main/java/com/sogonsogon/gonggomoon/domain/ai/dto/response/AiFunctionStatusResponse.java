package com.sogonsogon.gonggomoon.domain.ai.dto.response;


import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;

public record AiFunctionStatusResponse(
    AiFunctions type,
    Long id,
    AiFunctionStatus status,
    String error
) {
}
