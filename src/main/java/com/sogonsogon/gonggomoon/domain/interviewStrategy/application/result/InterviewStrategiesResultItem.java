package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * InterviewStrategy list 객체입니다.
 */
@Builder
public record InterviewStrategiesResultItem(
        UUID interviewStrategyId,
        Instant createdAt
) {
    public static InterviewStrategiesResultItem from(InterviewStrategy interviewStrategy) {
        return InterviewStrategiesResultItem.builder()
                .interviewStrategyId(interviewStrategy.getPublicId())
                .createdAt(interviewStrategy.getCreatedAt())
                .build();
    }
}
