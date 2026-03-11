package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;

public record GenerateInterviewQuestionSetResult(
        Long interviewStrategyId
) {
    public static GenerateInterviewQuestionSetResult from (InterviewStrategy interviewStrategy) {
        return new GenerateInterviewQuestionSetResult(interviewStrategy.getId());
    }
}