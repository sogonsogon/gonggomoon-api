package com.sogonsogon.gonggomoon.domain.strategy.generator.result;

import com.sogonsogon.gonggomoon.domain.strategy.domain.QuestionLevel;

public record InterviewQuestionItem(
        String question,
        QuestionLevel questionLevel
) {
}
