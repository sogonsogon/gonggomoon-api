package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import lombok.Builder;

@Builder
public record PortfolioStrategyAvailabilityResult(
        int usedCount,
        int limitCount,
        boolean canGenerate
) {
    public static PortfolioStrategyAvailabilityResult of (
            int usedCount, // 생성한 횟수
            int limitCount, // 제한 횟수
            boolean canGenerate
    ) {
        return PortfolioStrategyAvailabilityResult.builder()
                .usedCount(usedCount)
                .limitCount(limitCount)
                .canGenerate(canGenerate)
                .build();
    }
}
