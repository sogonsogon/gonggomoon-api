package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import lombok.Builder;

@Builder
public record InterviewStrategyAvailabilityResult(
        int usedCount,
        int limitCount,
        boolean canGenerate
) {
    public static InterviewStrategyAvailabilityResult of (
            int usedCount, // 생성한 횟수
            int limitCount, // 제한 횟수
            boolean canGenerate
    ) {
        return InterviewStrategyAvailabilityResult.builder()
                .usedCount(usedCount)
                .limitCount(limitCount)
                .canGenerate(canGenerate)
                .build();
    }
}
