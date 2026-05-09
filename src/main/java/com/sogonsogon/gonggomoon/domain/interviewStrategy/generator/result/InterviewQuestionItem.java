package com.sogonsogon.gonggomoon.domain.interviewStrategy.generator.result;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.QuestionLevel;

public record InterviewQuestionItem(
        String question,
        QuestionLevel questionLevel
) {
}
