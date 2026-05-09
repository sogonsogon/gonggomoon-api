package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import lombok.Builder;

@Builder
public record GeneratePortfolioStrategyResult(
        Long strategyId
) {
    public static GeneratePortfolioStrategyResult from (Long strategyId) {
        return GeneratePortfolioStrategyResult.builder()
                .strategyId(strategyId)
                .build();
    }
}
