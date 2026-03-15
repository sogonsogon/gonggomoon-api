package com.sogonsogon.gonggomoon.domain.ai.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum InterviewStrategyResultMapperError implements BaseErrorCode {
    QUESTIONS_ONLY_ARRAY("AI_QUESTIONS_ONLY_ARRAY", HttpStatus.BAD_REQUEST, "questions 필드는 배열이어야 합니다."),
    INVALID_QUESTION_FIELD("AI_INVALID_QUESTION_FIELD", HttpStatus.BAD_REQUEST, "question 필드는 null이거나 빈 문자열일 수 없습니다."),
    INVALID_QUESTION_LEVEL("AI_INVALID_QUESTION_LEVEL", HttpStatus.BAD_REQUEST, "질문 난이도 값이 올바르지 않습니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    InterviewStrategyResultMapperError(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
