package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.support;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import org.springframework.stereotype.Component;

@Component
public class PortfolioStrategyAvailabilityCalculator {

    public PortfolioStrategyAvailabilityResult calculate(
            int usedCount,
            int limitCount,
            boolean limitEnabled
    ) {
        boolean canGenerate = !limitEnabled || usedCount < limitCount;

        return PortfolioStrategyAvailabilityResult.of(
                usedCount,
                limitCount,
                canGenerate
        );
    }
}
