package com.sogonsogon.gonggomoon.global.error;

public class BaseException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public BaseException(BaseErrorCode errorCode) {
        super(errorCode.getMessage()); // 기본 메시지를 exception 메시지로 사용
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {return errorCode;}
}
