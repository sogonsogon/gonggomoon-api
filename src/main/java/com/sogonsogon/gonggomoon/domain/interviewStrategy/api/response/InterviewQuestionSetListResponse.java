package com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategiesResultItem;
import lombok.Builder;

import java.util.List;

@Builder
public record InterviewQuestionSetListResponse(
        int totalCount,
        List<InterviewStrategiesResultItem> contents
) {
    public static InterviewQuestionSetListResponse from (InterviewQuestionSetListResult result) {
        return InterviewQuestionSetListResponse.builder()
                .totalCount(result.totalCount())
                .contents(result.contents())
                .build();
    }
}
