package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import lombok.Builder;

import java.util.List;

@Builder
public record PortfolioStrategyListResult(
        int totalCount,
        List<PortfolioStrategyListResultItem> contents
) {
    public static PortfolioStrategyListResult from(List<PortfolioStrategyListResultItem> items) {
        return PortfolioStrategyListResult.builder()
                .totalCount(items.size())
                .contents(items)
                .build();
    }
}
