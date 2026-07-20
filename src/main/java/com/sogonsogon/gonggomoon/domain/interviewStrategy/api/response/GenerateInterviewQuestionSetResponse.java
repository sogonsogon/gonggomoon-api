package com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.GenerateInterviewQuestionSetResult;
import java.util.UUID;

public record GenerateInterviewQuestionSetResponse(
        UUID interviewStrategyId
) {
    public static GenerateInterviewQuestionSetResponse from (GenerateInterviewQuestionSetResult result) {
        return new GenerateInterviewQuestionSetResponse(
                result.interviewStrategyId()
        );
    }
}
