package com.sogonsogon.gonggomoon.domain.strategy.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum InterviewStrategyErrorCode implements BaseErrorCode {
    NOT_FOUND("INTERVIEW_STRATEGY_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 면접 전략을 찾을 수 없습니다."),
    FILE_ASSET_NOT_FOUND("INTERVIEW_STRATEGY_FILE_ASSET_NOT_FOUND", HttpStatus.BAD_REQUEST, "해당 파일을 찾을 수 없습니다."),
    FILE_ASSET_ID_REQUIRED("INTERVIEW_STRATEGY_FILE_ASSET_ID_REQUIRED", HttpStatus.BAD_REQUEST, "파일 선택은 필수입니다."),
    INVALID_PORTFOLIO_FILE("INTERVIEW_STRATEGY_INVALID_PORTFOLIO_FILE", HttpStatus.BAD_REQUEST, "포트폴리오 파일만 사용할 수 있습니다."),
    QUESTION_JSON_SERIALIZATION_FAILED("INTERVIEW_STRATEGY_QUESTION_JSON_SERIALIZATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "질문 결과 JSON 직렬화에 실패했습니다."),
    INVALID_INTERVIEW_QUESTION("INTERVIEW_STRATEGY_INVALID_INTERVIEW_QUESTION", HttpStatus.BAD_REQUEST, "면접 질문이 올바르지 않습니다."),
    INVALID_QUESTION_LEVEL("INTERVIEW_STRATEGY_INVALID_QUESTION_LEVEL", HttpStatus.BAD_REQUEST, "질문 난이도가 올바르지 않습니다."),
    USER_ID_REQUIRED("INTERVIEW_STRATEGY_USER_ID_REQUIRED", HttpStatus.BAD_REQUEST, "유저 아이디는 필수입니다."),
    ALREADY_CREATED_TODAY("INTERVIEW_STRATEGY_ALREADY_CREATED_TODAY", HttpStatus.CONFLICT, "오늘은 이미 면접 질문을 생성했습니다."),
    RESULT_NOT_READY("INTERVIEW_STRATEGY_RESULT_NOT_READY", HttpStatus.ACCEPTED, "면접 질문이 아직 준비되지 않았습니다."),
    GENERATION_FAILED("INTERVIEW_STRATEGY_GENERATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "면접 질문 생성에 실패했습니다."),
    QUESTION_EMPTY("INTERVIEW_STRATEGY_QUESTION_EMPTY", HttpStatus.INTERNAL_SERVER_ERROR, "면접 질문이 비어있습니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    InterviewStrategyErrorCode(String code, HttpStatus status, String message) {
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
