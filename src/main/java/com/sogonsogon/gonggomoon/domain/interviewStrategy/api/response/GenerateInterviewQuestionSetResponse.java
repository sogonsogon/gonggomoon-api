package com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.GenerateInterviewQuestionSetResult;

public record GenerateInterviewQuestionSetResponse(
        Long interviewStrategyId
) {
    public static GenerateInterviewQuestionSetResponse from (GenerateInterviewQuestionSetResult result) {
        return new GenerateInterviewQuestionSetResponse(
                result.interviewStrategyId()
        );
    }
}
