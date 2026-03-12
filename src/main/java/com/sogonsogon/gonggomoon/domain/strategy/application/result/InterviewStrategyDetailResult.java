package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record InterviewStrategyDetailResult(
        Long interviewStrategyId,
        String basePortfolio,
        Instant createdAt,
        int questionTotalCount,
        List<InterviewStrategyDetailResultItem> contents
) {
    public static InterviewStrategyDetailResult of (
            InterviewStrategy interviewStrategy, FileAsset fileAsset) {

        List<InterviewStrategyDetailResultItem> items = interviewStrategy.getQuestions().stream()
                .map(InterviewStrategyDetailResultItem::from)
                .toList();

        return InterviewStrategyDetailResult.builder()
                .interviewStrategyId(interviewStrategy.getId())
                .basePortfolio(fileAsset.getOriginalFileName())
                .createdAt(interviewStrategy.getCreatedAt())
                .questionTotalCount(items.size())
                .contents(items)
                .build();
    }
}
