package com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyDetailResultItem;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record InterviewStrategyDetailResponse(
        Long interviewStrategyId,
        String basePortfolio,
        Instant createdAt,
        int questionTotalCount,
        List<InterviewStrategyDetailResultItem> contents
) {
    public static InterviewStrategyDetailResponse from (InterviewStrategyDetailResult result) {
        return InterviewStrategyDetailResponse.builder()
                .interviewStrategyId(result.interviewStrategyId())
                .basePortfolio(result.basePortfolio())
                .createdAt(result.createdAt())
                .questionTotalCount(result.questionTotalCount())
                .contents(result.contents())
                .build();
    }
}
