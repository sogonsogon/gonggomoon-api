package com.sogonsogon.gonggomoon.domain.ai.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ExtractedExperienceErrorCode implements BaseErrorCode {
    NOT_FOUND("EXTRACTED_EXPERIENCE_NOT_FOUND", HttpStatus.NOT_FOUND, "추출된 경험을 찾을 수 없습니다."),
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    ExtractedExperienceErrorCode(String code, HttpStatus status, String message) {
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
