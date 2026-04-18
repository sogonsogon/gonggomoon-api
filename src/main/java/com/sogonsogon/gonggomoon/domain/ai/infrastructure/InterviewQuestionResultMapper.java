package com.sogonsogon.gonggomoon.domain.ai.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.sogonsogon.gonggomoon.domain.ai.error.InterviewStrategyResultMapperError;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.QuestionLevel;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InterviewQuestionResultMapper {

    public List<InterviewQuestion> toInterviewQuestions(JsonNode questionsNode, InterviewStrategy interviewStrategy) {
        if (questionsNode == null || !questionsNode.isArray()) {
            throw new BaseException(InterviewStrategyResultMapperError.QUESTIONS_ONLY_ARRAY);
        }

        List<InterviewQuestion> questions = new ArrayList<>();

        for (JsonNode node : questionsNode) {
            questions.add(toInterviewQuestion(node, interviewStrategy));
        }

        return questions;
    }

    private InterviewQuestion toInterviewQuestion(JsonNode node, InterviewStrategy interviewStrategy) {
        return InterviewQuestion.builder()
            .question(getText(node, "question"))
            .questionLevel(parseQuestionLevel(node.get("questionLevel")))
            .interviewStrategy(interviewStrategy)
            .build();
    }

    private String getText(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);

        if (value == null || value.isNull()) {
            throw new BaseException(InterviewStrategyResultMapperError.INVALID_QUESTION_FIELD);
        }

        String text = value.asText();
        if (text == null || text.isBlank()) {
            throw new BaseException(InterviewStrategyResultMapperError.INVALID_QUESTION_FIELD);
        }

        return text.trim();
    }

    private QuestionLevel parseQuestionLevel(JsonNode node) {
        if (node == null || node.isNull()) {
            throw new BaseException(InterviewStrategyResultMapperError.INVALID_QUESTION_LEVEL);
        }

        String value = node.asText();
        if (value == null || value.isBlank()) {
            throw new BaseException(InterviewStrategyResultMapperError.INVALID_QUESTION_LEVEL);
        }

        try {
            return QuestionLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BaseException(InterviewStrategyResultMapperError.INVALID_QUESTION_LEVEL);
        }
    }
}
