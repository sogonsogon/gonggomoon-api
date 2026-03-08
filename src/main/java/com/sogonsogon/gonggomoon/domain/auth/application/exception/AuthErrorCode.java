package com.sogonsogon.gonggomoon.domain.auth.application.exception;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements BaseErrorCode {
    OAUTH_UNLINK_FAIL("AUTH_OAUTH_UNLINK_FAIL", HttpStatus.INTERNAL_SERVER_ERROR, "회원 탈퇴에 문제가 발생했습니다."),
    REFRESH_TOKEN_INVALID("AUTH_REFRESH_TOKEN_INVALID", HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
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
