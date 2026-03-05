package com.sogonsogon.gonggomoon.domain.auth.domain.dto;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements BaseErrorCode {
    UNSUPPORTED_PROVIDER("AUTH_UNSUPPORTED_PROVIDER", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다."),
    INVALID_VALUE("AUTH_INVALID_VALUE", HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다.")

    ;
    private final String code;
    private final HttpStatus status;
    private final String message;

    AuthErrorCode(String code, HttpStatus status, String message) {
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
