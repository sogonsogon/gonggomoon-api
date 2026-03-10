package com.sogonsogon.gonggomoon.domain.post.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PlatformErrorCode implements BaseErrorCode {

    PLATFORM_NOT_FOUND("PLATFORM_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 플랫폼입니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    PlatformErrorCode(String code, HttpStatus status, String message) {
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
