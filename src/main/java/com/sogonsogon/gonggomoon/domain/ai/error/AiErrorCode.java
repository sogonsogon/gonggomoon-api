package com.sogonsogon.gonggomoon.domain.ai.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum AiErrorCode implements BaseErrorCode {
    AI_SERVER_ERROR("AI_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "AI 서버에서 오류가 발생했습니다."),
    INVALID_TYPE("AI_INVALID_TYPE", HttpStatus.BAD_REQUEST, "유효하지 않은 type입니다.")
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    AiErrorCode(String code, HttpStatus httpStatus, String message) {
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
