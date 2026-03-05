package com.sogonsogon.gonggomoon.domain.auth.application.exception;

import com.sogonsogon.gonggomoon.global.error.BaseException;

public class RefreshTokenInvalidException extends BaseException {
    public RefreshTokenInvalidException() {
        super(AuthErrorCode.REFRESH_TOKEN_INVALID);
    }
}
