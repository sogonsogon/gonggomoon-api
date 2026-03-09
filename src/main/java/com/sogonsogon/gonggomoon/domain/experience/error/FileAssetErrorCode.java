package com.sogonsogon.gonggomoon.domain.experience.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum FileAssetErrorCode implements BaseErrorCode {
    NOT_FOUND("FILE_ASSET_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),
    FILE_REQUIRED("FILE_ASSET_FILE_REQUIRED", HttpStatus.BAD_REQUEST, "업로드 파일은 필수입니다."),
    EMPTY_FILE_NOT_ALLOWED("FILE_ASSET_EMPTY_FILE_NOT_ALLOWED", HttpStatus.BAD_REQUEST, "비어 있는 파일은 업로드할 수 없습니다."),
    INVALID_FILE_NAME("FILE_ASSET_INVALID_FILE_NAME", HttpStatus.BAD_REQUEST, "유효하지 않은 파일 이름입니다."),
    FILE_SIZE_EXCEEDED("FILE_ASSET_FILE_SIZE_EXCEEDED", HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 허용 용량을 초과했습니다."),

    USER_ID_REQUIRED("FILE_ASSET_USER_ID_REQUIRED", HttpStatus.BAD_REQUEST, "사용자 ID는 필수입니다."),
    CATEGORY_REQUIRED("FILE_ASSET_CATEGORY_REQUIRED", HttpStatus.BAD_REQUEST, "파일 카테고리는 필수입니다."),
    ORIGINAL_FILE_NAME_REQUIRED("FILE_ASSET_ORIGINAL_FILE_NAME_REQUIRED", HttpStatus.BAD_REQUEST, "원본 파일 이름은 필수입니다."),
    FILE_KEY_REQUIRED("FILE_ASSET_FILE_KEY_REQUIRED", HttpStatus.BAD_REQUEST, "파일 키는 필수입니다."),
    INVALID_FILE_SIZE("FILE_ASSET_INVALID_FILE_SIZE", HttpStatus.BAD_REQUEST, "파일 크기는 0보다 커야 합니다."),
    FILE_STREAM_READ_FAILED("EXPERIENCE_FILE_STREAM_READ_FALIED", HttpStatus.INTERNAL_SERVER_ERROR, "파일 스트림을 읽는 중 오류가 발생했습니다."),
    S3_UPLOAD_FAILED("EXPERIENCE_S3_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장소에 업로드하는 중 오류가 발생했습니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    FileAssetErrorCode(String code, HttpStatus status, String message) {
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
