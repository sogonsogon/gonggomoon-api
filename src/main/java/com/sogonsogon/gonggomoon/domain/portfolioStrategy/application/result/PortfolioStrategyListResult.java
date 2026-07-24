package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import lombok.Builder;

import java.util.List;

@Builder
public record PortfolioStrategyListResult(
        String nextCursor,
        boolean hasNext,
        List<PortfolioStrategyListResultItem> contents
) {
    public static PortfolioStrategyListResult of(
            List<PortfolioStrategyListResultItem> items,
            String nextCursor,
            boolean hasNext
    ) {
        return PortfolioStrategyListResult.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .contents(items)
                .build();
    }
}
