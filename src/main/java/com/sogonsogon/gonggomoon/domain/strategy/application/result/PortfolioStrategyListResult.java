package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import lombok.Builder;

import java.util.List;

@Builder
public record PortfolioStrategyListResult(
        int totalCount,
        List<PortfolioStrategyListResultItem> contents
) {
    public static PortfolioStrategyListResult from(List<PortfolioStrategy> portfolioStrategies) {
        List<PortfolioStrategyListResultItem> items = portfolioStrategies.stream()
                .map(PortfolioStrategyListResultItem::from)
                .toList();

        return PortfolioStrategyListResult.builder()
                .totalCount(items.size())
                .contents(items)
                .build();
    }
}
