package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import java.util.UUID;

public record GenerateInterviewQuestionSetResult(
        UUID interviewStrategyId
) {
    public static GenerateInterviewQuestionSetResult from (InterviewStrategy interviewStrategy) {
        return new GenerateInterviewQuestionSetResult(interviewStrategy.getPublicId());
    }
}
