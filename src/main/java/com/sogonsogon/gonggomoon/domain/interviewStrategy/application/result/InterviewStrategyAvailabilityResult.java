package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import lombok.Builder;

@Builder
public record InterviewStrategyAvailabilityResult(
        // 이번 주에 이미 점유한 AI 생성 횟수입니다.
        int usedCount,
        // 이번 주에 허용되는 최대 AI 생성 횟수입니다.
        int limitCount,
        // 새 AI 생성 요청을 시작할 수 있는지 여부입니다.
        boolean canGenerate,
        // 기존 실패/중단 작업의 재시도 가능 여부입니다. 현재는 재시도 API/정책이 분리되어 있지 않아 항상 true입니다.
        boolean canRetry
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
                .canRetry(true)
                .build();
    }
}
