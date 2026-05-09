package com.sogonsogon.gonggomoon.domain.post.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PostErrorCode implements BaseErrorCode {

    POST_NOT_FOUND("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 공고입니다."),
    POST_NOT_PUBLISHED("POST_NOT_PUBLISHED", HttpStatus.FORBIDDEN, "접근할 수 없는 공고입니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    PostErrorCode(String code, HttpStatus status, String message) {
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
