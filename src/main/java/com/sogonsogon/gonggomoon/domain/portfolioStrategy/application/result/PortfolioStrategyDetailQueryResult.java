package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import java.util.UUID;

public record PortfolioStrategyDetailQueryResult(
        PortfolioStrategy portfolioStrategy,
        UUID postId,
        UUID postAnalysisId,
        String postAnalysisTitle
) {
}
