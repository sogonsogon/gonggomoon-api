package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import lombok.Builder;

@Builder
public record GeneratePortfolioStrategyResult(
        Long strategyId
) {
    public static GeneratePortfolioStrategyResult of (Long strategyId) {
        return GeneratePortfolioStrategyResult.builder()
                .strategyId(strategyId)
                .build();
    }
}
