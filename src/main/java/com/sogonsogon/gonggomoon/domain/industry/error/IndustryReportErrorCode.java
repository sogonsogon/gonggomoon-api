package com.sogonsogon.gonggomoon.domain.industry.error;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum IndustryReportErrorCode implements BaseErrorCode {

    INDUSTRY_REPORT_NOT_FOUND("INDUSTRY_REPORT_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 산업 보고서입니다."),
    INDUSTRY_REPORT_NOT_PUBLISHED("INDUSTRY_REPORT_NOT_PUBLISHED", HttpStatus.FORBIDDEN, "접근할 수 없는 보고서 입니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    IndustryReportErrorCode(String code, HttpStatus status, String message) {
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
