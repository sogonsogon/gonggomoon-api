package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;

public record PortfolioStrategyDetailQueryResult(
        PortfolioStrategy portfolioStrategy,
        String postAnalysisTitle
) {
}
