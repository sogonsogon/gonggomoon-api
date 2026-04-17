package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import lombok.Builder;

import java.util.List;

@Builder
public record InterviewQuestionSetListResult(
        int totalCount,
        List<InterviewStrategiesResultItem> contents
) {
    public static InterviewQuestionSetListResult from(List<InterviewStrategy> interviewStrategies) {
        List<InterviewStrategiesResultItem> items = interviewStrategies.stream()
                .map(InterviewStrategiesResultItem::from)
                .toList();

        return InterviewQuestionSetListResult.builder()
                .totalCount(items.size())
                .contents(items)
                .build();
    }
}
