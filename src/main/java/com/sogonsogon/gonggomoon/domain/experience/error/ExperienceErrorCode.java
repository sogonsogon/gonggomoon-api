package com.sogonsogon.gonggomoon.domain.experience.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ExperienceErrorCode implements BaseErrorCode {
    NOT_FOUND("EXPERIENCE_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 경험을 찾을 수 없습니다."),
    INVALID_VALUE("EXPERIENCE_INVALID_VALUE", HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),
    USERID_REQUIRED("EXPERIENCE_USERID_REQUIRED", HttpStatus.BAD_REQUEST, "유저 아이디는 필수입니다."),
    TITLE_REQUIRED("EXPERIENCE_TITLE_REQUIRED", HttpStatus.BAD_REQUEST, "제목은 필수입니다."),
    TYPE_REQUIRED("EXPERIENCE_TYPE_REQUIRED", HttpStatus.BAD_REQUEST, "경험 유형은 필수입니다."),
    CONTENT_REQUIRED("EXPERIENCE_CONTENT_REQUIRED", HttpStatus.BAD_REQUEST, "경험 내용은 필수입니다."),
    INVALID_DATE_RANGE("EXPERIENCE_INVALID_DATE_RANGE", HttpStatus.BAD_REQUEST, "종료일이 시작일보다 이전일 수 없습니다.")

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    ExperienceErrorCode(String code, HttpStatus status, String message) {
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
