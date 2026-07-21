package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.GeneratePortfolioStrategyResult;
import lombok.Builder;
import java.util.UUID;

@Builder
public record GeneratePortfolioStrategyResponse(
        UUID strategyId
) {
    public static GeneratePortfolioStrategyResponse from (GeneratePortfolioStrategyResult result) {
        return GeneratePortfolioStrategyResponse.builder()
                .strategyId(result.strategyId())
                .build();
    }
}
