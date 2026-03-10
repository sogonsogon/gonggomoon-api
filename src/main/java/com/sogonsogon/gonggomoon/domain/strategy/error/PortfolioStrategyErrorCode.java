package com.sogonsogon.gonggomoon.domain.strategy.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PortfolioStrategyErrorCode implements BaseErrorCode {
    EXPERIENCE_IDS_REQUIRED("PORTFOLIO_STRATEGY_EXPERIENCE_IDS_REQUIRED", HttpStatus.BAD_REQUEST, "경험 선택은 필수입니다."),
    TOO_MANY_EXPERIENCES("PORTFOLIO_STRATEGY_TOO_MANY_EXPERIENCES", HttpStatus.BAD_REQUEST, "경험은 최대 2개까지 선택할 수 있습니다."),
    RESULT_JSON_SERIALIZATION_FAILED("PORTFOLIO_STRATEGY_RESULT_JSON_SERIALIZATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "전략 결과 JSON 직렬화에 실패했습니다."),
    USERID_REQUIRED("PORTFOLIO_STRATEGY_USERID_REQUIRED", HttpStatus.BAD_REQUEST, "유저 아이디는 필수입니다."),
    JOB_TYPE_REQUIRED("PORTFOLIO_STRATEGY_JOB_TYPE_REQUIRED", HttpStatus.BAD_REQUEST, "직무 타입은 필수입니다."),
    REQUESTED_EXPERIENCE_NOT_FOUND("PORTFOLIO_STRATEGY_REQUESTED_EXPERIENCE_NOT_FOUND", HttpStatus.BAD_REQUEST, "요청한 경험 정보를 찾을 수 없습니다.")
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
