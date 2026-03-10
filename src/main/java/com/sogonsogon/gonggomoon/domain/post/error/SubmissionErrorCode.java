package com.sogonsogon.gonggomoon.domain.post.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum SubmissionErrorCode implements BaseErrorCode {

    INVALID_SUBMISSION("INVALID_SUBMISSION", HttpStatus.BAD_REQUEST, "공고 신청 정보가 올바르지 않습니다."),
    INVALID_URL_FORMAT("INVALID_URL_FORMAT", HttpStatus.BAD_REQUEST, "올바른 URL 형식이 아닙니다."),
    URL_PLATFORM_MISMATCH("URL_PLATFORM_MISMATCH", HttpStatus.BAD_REQUEST, "해당 플랫폼의 URL이 아닙니다."),
    DUPLICATE_URL("DUPLICATE_URL", HttpStatus.CONFLICT, "이미 등록된 공고 URL입니다."),
    DUPLICATE_SUBMISSION("DUPLICATE_SUBMISSION", HttpStatus.CONFLICT, "이미 요청 중인 공고입니다."),
    ;


    private final String code;
    private final HttpStatus status;
    private final String message;

    SubmissionErrorCode(String code, HttpStatus status, String message) {
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
