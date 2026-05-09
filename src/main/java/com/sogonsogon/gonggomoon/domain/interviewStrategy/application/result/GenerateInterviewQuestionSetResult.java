package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;

public record GenerateInterviewQuestionSetResult(
        Long interviewStrategyId
) {
    public static GenerateInterviewQuestionSetResult from (InterviewStrategy interviewStrategy) {
        return new GenerateInterviewQuestionSetResult(interviewStrategy.getId());
    }
}