package com.sogonsogon.gonggomoon.domain.strategy.application.support;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.StrategyAvailabilityResult;
import org.springframework.stereotype.Component;

@Component
public class StrategyAvailabilityCalculator {

    public StrategyAvailabilityResult calculate(
            int usedCount,
            int limitCount,
            boolean dailyLimitEnabled
    ) {
        int remainingCount = dailyLimitEnabled
                ? Math.max(0, limitCount - usedCount)
                : limitCount;

        boolean canGenerate = !dailyLimitEnabled || remainingCount > 0;

        return StrategyAvailabilityResult.of(
                usedCount,
                limitCount,
                canGenerate
        );
    }
}
