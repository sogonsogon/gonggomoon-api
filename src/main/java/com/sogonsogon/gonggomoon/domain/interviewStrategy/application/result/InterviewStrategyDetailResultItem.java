package com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.QuestionLevel;
import lombok.Builder;

@Builder
public record InterviewStrategyDetailResultItem(
        Long questionId,
        String question,
        QuestionLevel questionLevel
) {
    public static InterviewStrategyDetailResultItem from (InterviewQuestion interviewQuestion) {
        return InterviewStrategyDetailResultItem.builder()
                .questionId(interviewQuestion.getId())
                .question(interviewQuestion.getQuestion())
                .questionLevel(interviewQuestion.getQuestionLevel())
                .build();
    }
}
