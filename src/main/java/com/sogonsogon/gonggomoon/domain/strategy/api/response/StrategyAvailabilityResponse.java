package com.sogonsogon.gonggomoon.domain.strategy.api.response;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.StrategyAvailabilityResult;
import lombok.Builder;

@Builder
public record StrategyAvailabilityResponse(
        int usedCount,
        int limitCount,
        boolean canGenerate
) {
    public static StrategyAvailabilityResponse from (StrategyAvailabilityResult result) {
        return StrategyAvailabilityResponse.builder()
                .usedCount(result.usedCount())
                .limitCount(result.limitCount())
                .canGenerate(result.canGenerate())
                .build();
    }
}
