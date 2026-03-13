package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import lombok.Builder;

@Builder
public record StrategyAvailabilityResult(
        int usedCount,
        int limitCount,
        boolean canGenerate
) {
    public static StrategyAvailabilityResult of (
            int usedCount, // 생성한 횟수
            int limitCount, // 제한 횟수
            boolean canGenerate
    ) {
        return StrategyAvailabilityResult.builder()
                .usedCount(usedCount)
                .limitCount(limitCount)
                .canGenerate(canGenerate)
                .build();
    }
}
