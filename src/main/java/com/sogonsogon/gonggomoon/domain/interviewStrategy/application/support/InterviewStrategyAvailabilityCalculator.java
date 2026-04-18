package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.support;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import org.springframework.stereotype.Component;

@Component
public class InterviewStrategyAvailabilityCalculator {

    public InterviewStrategyAvailabilityResult calculate(
            int usedCount,
            int limitCount,
            boolean dailyLimitEnabled
    ) {
        int remainingCount = dailyLimitEnabled
                ? Math.max(0, limitCount - usedCount)
                : limitCount;

        boolean canGenerate = !dailyLimitEnabled || remainingCount > 0;

        return InterviewStrategyAvailabilityResult.of(
                usedCount,
                limitCount,
                canGenerate
        );
    }
}
