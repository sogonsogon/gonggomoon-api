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
    INVALID_DATE_RANGE("EXPERIENCE_INVALID_DATE_RANGE", HttpStatus.BAD_REQUEST, "종료일이 시작일보다 이전일 수 없습니다."),
    FILE_ASSET_NOT_FOUND("EXPERIENCE_FILE_ASSET_NOT_FOUND", HttpStatus.BAD_REQUEST, "해당 파일을 찾을 수 없습니다."),
    FILE_ASSET_COUNT_EXCEEDED("EXPERIENCE_FILE_ASSET_COUNT_EXCEEDED", HttpStatus.BAD_REQUEST, "파일은 최대 2개까지 요청할 수 있습니다."),
    DUPLICATE_FILE_ASSET_ID("EXPERIENCE_DUPLICATE_FILE_ASSET_ID", HttpStatus.BAD_REQUEST, "중복된 fileAssetId는 허용되지 않습니다."),
    INVALID_FILE_ASSET_REQUEST("EXPERIENCE_INVALID_FILE_ASSET_REQUEST", HttpStatus.BAD_REQUEST, "요청한 파일 중 존재하지 않거나 본인 소유가 아닌 파일이 포함되어 있습니다.")
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
