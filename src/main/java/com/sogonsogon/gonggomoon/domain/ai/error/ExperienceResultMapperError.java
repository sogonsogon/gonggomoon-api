package com.sogonsogon.gonggomoon.domain.ai.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ExperienceResultMapperError implements BaseErrorCode {
    EXPERIENCES_ONLY_ARRAY("EXPERIENCES_ONLY_ARRAY", HttpStatus.BAD_REQUEST, "경험 결과는 배열 형태여야 합니다."),
    INVALID_EXPERIENCE_TYPE("INVALID_EXPERIENCE_TYPE", HttpStatus.BAD_REQUEST, "유효하지 않은 경험 유형입니다."),
    DATE_FORMAT_ERROR("DATE_FORMAT_ERROR", HttpStatus.BAD_REQUEST, "날짜 형식이 올바르지 않습니다. (예: 2023-01)")
    ;


    private final String message;
    private final HttpStatus status;
    private final String code;

    ExperienceResultMapperError(String code, HttpStatus status, String message) {
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
