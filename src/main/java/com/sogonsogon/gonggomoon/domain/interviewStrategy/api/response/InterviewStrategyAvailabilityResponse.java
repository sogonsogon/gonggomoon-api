package com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import lombok.Builder;

@Builder
public record InterviewStrategyAvailabilityResponse(
        int usedCount,
        int limitCount,
        boolean canGenerate,
        boolean canRetry
) {
    public static InterviewStrategyAvailabilityResponse from (InterviewStrategyAvailabilityResult result) {
        return InterviewStrategyAvailabilityResponse.builder()
                .usedCount(result.usedCount())
                .limitCount(result.limitCount())
                .canGenerate(result.canGenerate())
                .canRetry(result.canRetry())
                .build();
    }
}
