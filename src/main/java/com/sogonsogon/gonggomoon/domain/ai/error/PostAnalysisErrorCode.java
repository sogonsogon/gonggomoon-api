package com.sogonsogon.gonggomoon.domain.ai.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PostAnalysisErrorCode implements BaseErrorCode {
    INVALID_CALLBACK_FORMAT("INVALID_CALLBACK_FORMAT", HttpStatus.BAD_REQUEST, "콜백 결과 형식이 올바르지 않습니다."),
    NOT_FOUND("POST_ANALYSIS_NOT_FOUND", HttpStatus.NOT_FOUND, "공고 분석 결과를 찾을 수 없습니다."),
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    PostAnalysisErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
