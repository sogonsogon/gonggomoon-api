package com.sogonsogon.gonggomoon.domain.strategy.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PortfolioStrategyErrorCode implements BaseErrorCode {
    NOT_FOUND("PORTFOLIO_STRATEGY_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 포트폴리오 전략을 찾을 수 없습니다."),
    EXPERIENCE_IDS_REQUIRED("PORTFOLIO_STRATEGY_EXPERIENCE_IDS_REQUIRED", HttpStatus.BAD_REQUEST, "경험 선택은 필수입니다."),
    RESULT_JSON_SERIALIZATION_FAILED("PORTFOLIO_STRATEGY_RESULT_JSON_SERIALIZATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "전략 결과 JSON 직렬화에 실패했습니다."),
    RESULT_JSON_DESERIALIZATION_FAILED("PORTFOLIO_STRATEGY_RESULT_JSON_DESERIALIZATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "전략 결과 JSON 역직렬화에 실패했습니다."),
    USERID_REQUIRED("PORTFOLIO_STRATEGY_USERID_REQUIRED", HttpStatus.BAD_REQUEST, "유저 아이디는 필수입니다."),
    JOB_TYPE_REQUIRED("PORTFOLIO_STRATEGY_JOB_TYPE_REQUIRED", HttpStatus.BAD_REQUEST, "직무 타입은 필수입니다."),
    REQUESTED_EXPERIENCE_NOT_FOUND("PORTFOLIO_STRATEGY_REQUESTED_EXPERIENCE_NOT_FOUND", HttpStatus.BAD_REQUEST, "요청한 경험 정보를 찾을 수 없습니다."),
    INVALID_JOB_TYPE("PORTFOLIO_STRATEGY_INVALID_JOB_TYPE", HttpStatus.BAD_REQUEST, "올바르지 않은 직무 타입입니다."),
    ALREADY_CREATED_TODAY("PORTFOLIO_STRATEGY_ALREADY_CREATED_TODAY", HttpStatus.CONFLICT, "오늘은 이미 포트폴리오 전략을 생성했습니다."),
    RESULT_JSON_EMPTY("PORTFOLIO_STRATEGY_RESULT_JSON_EMPTY", HttpStatus.INTERNAL_SERVER_ERROR, "전략 결과 JSON이 비어 있습니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    PortfolioStrategyErrorCode(String code, HttpStatus status, String message) {
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
