package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.support;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import org.springframework.stereotype.Component;

@Component
public class InterviewStrategyAvailabilityCalculator {

    public InterviewStrategyAvailabilityResult calculate(
            int usedCount,
            int limitCount,
            boolean limitEnabled
    ) {
        boolean canGenerate = !limitEnabled || usedCount < limitCount;

        return InterviewStrategyAvailabilityResult.of(
                usedCount,
                limitCount,
                canGenerate
        );
    }
}
