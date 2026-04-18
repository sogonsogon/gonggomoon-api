package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.support;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import org.springframework.stereotype.Component;

@Component
public class PortfolioStrategyAvailabilityCalculator {

    public PortfolioStrategyAvailabilityResult calculate(
            int usedCount,
            int limitCount,
            boolean dailyLimitEnabled
    ) {
        int remainingCount = dailyLimitEnabled
                ? Math.max(0, limitCount - usedCount)
                : limitCount;

        boolean canGenerate = !dailyLimitEnabled || remainingCount > 0;

        return PortfolioStrategyAvailabilityResult.of(
                usedCount,
                limitCount,
                canGenerate
        );
    }
}
