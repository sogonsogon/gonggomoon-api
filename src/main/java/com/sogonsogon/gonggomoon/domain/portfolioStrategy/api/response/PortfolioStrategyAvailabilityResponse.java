package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import lombok.Builder;

@Builder
public record PortfolioStrategyAvailabilityResponse(
        int usedCount,
        int limitCount,
        boolean canGenerate
) {
    public static PortfolioStrategyAvailabilityResponse from (PortfolioStrategyAvailabilityResult result) {
        return PortfolioStrategyAvailabilityResponse.builder()
                .usedCount(result.usedCount())
                .limitCount(result.limitCount())
                .canGenerate(result.canGenerate())
                .build();
    }
}
