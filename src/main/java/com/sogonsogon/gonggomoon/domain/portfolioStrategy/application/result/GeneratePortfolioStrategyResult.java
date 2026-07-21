package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import lombok.Builder;
import java.util.UUID;

@Builder
public record GeneratePortfolioStrategyResult(
        UUID strategyId
) {
    public static GeneratePortfolioStrategyResult from (UUID strategyId) {
        return GeneratePortfolioStrategyResult.builder()
                .strategyId(strategyId)
                .build();
    }
}
