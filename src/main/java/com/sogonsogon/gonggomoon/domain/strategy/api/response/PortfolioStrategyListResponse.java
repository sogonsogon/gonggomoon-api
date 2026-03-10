package com.sogonsogon.gonggomoon.domain.strategy.api.response;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResultItem;
import lombok.Builder;

import java.util.List;

@Builder
public record PortfolioStrategyListResponse(
        int totalCount,
        List<PortfolioStrategyListResultItem> contents
) {
    public static PortfolioStrategyListResponse from (PortfolioStrategyListResult result) {
        return PortfolioStrategyListResponse.builder()
                .totalCount(result.totalCount())
                .contents(result.contents())
                .build();
    }
}
