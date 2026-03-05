package com.sogonsogon.gonggomoon.global.error;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements BaseErrorCode {
    INTERNAL_SERVER_ERROR("GLOBAL_INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE("GLOBAL_INVALID_INPUT_VALUE", HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
    MISSING_REQUEST_COOKIE("GLOBAL_MISSING_REQUEST_COOKIE", HttpStatus.BAD_REQUEST, "필수 쿠키가 누락되었습니다."),
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    GlobalErrorCode(String code, HttpStatus status, String message) {
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
