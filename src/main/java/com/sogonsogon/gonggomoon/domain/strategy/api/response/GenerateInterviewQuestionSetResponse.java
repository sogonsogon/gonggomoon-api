package com.sogonsogon.gonggomoon.domain.strategy.api.response;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;

public record GenerateInterviewQuestionSetResponse(
        Long interviewStrategyId
) {
    public static GenerateInterviewQuestionSetResponse from (GenerateInterviewQuestionSetResult result) {
        return new GenerateInterviewQuestionSetResponse(
                result.interviewStrategyId()
        );
    }
}
