package com.sogonsogon.gonggomoon.domain.bookmark.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum BookmarkErrorCode implements BaseErrorCode {
    BOOKMARK_ALREADY_EXISTS("BOOKMARK_ALREADY_EXISTS", HttpStatus.CONFLICT, "이미 존재하는 북마크입니다."),
    BOOKMARK_NOT_FOUND("BOOKMARK_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 북마크입니다."),

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    BookmarkErrorCode(String code, HttpStatus status, String message) {
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
