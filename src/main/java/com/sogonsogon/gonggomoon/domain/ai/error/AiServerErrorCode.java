package com.sogonsogon.gonggomoon.domain.ai.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum AiServerErrorCode implements BaseErrorCode {
    AI_SERVER_ERROR("AI_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "AI 서버에서 오류가 발생했습니다.")
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    AiServerErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }

    @Override
    public HttpStatus getStatus() {
        return null;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
