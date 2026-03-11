package com.sogonsogon.gonggomoon.domain.strategy.generator.result;

import java.util.List;

public record InterviewStrategyQuestionSet(
        List<InterviewQuestionItem> questions
) {
    public static InterviewStrategyQuestionSet of (List<InterviewQuestionItem> questions) {
        return new InterviewStrategyQuestionSet(questions);
    }
}
