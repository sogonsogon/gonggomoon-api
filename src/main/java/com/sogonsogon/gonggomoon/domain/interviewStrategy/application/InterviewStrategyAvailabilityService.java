package com.sogonsogon.gonggomoon.domain.interviewStrategy.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiUsageAvailability;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.support.InterviewStrategyAvailabilityCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewStrategyAvailabilityService {

    private final InterviewStrategyAvailabilityCalculator interviewStrategyAvailabilityCalculator;
    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${strategy.interview.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    public InterviewStrategyAvailabilityResult getAvailability(Long userId) {
        AiUsageAvailability availability = aiUsagePolicyService.getAvailability(
                userId,
                AiUsageType.INTERVIEW_STRATEGY,
                weeklyLimitEnabled
        );
        return interviewStrategyAvailabilityCalculator.calculate(
                availability.usedCount(),
                availability.limitCount(),
                weeklyLimitEnabled
        );
    }
}
