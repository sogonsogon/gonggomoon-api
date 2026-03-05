package com.sogonsogon.gonggomoon.domain.auth.domain.dto;

import com.sogonsogon.gonggomoon.global.error.BaseException;

public class AuthInvalidValue extends BaseException {
    public AuthInvalidValue() {
        super(AuthErrorCode.INVALID_VALUE);
    }
}
