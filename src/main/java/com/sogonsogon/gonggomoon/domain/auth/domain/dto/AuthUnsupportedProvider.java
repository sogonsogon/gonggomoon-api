package com.sogonsogon.gonggomoon.domain.auth.domain.dto;

import com.sogonsogon.gonggomoon.global.error.BaseException;

public class AuthUnsupportedProvider extends BaseException {
    public AuthUnsupportedProvider() {
        super(AuthErrorCode.UNSUPPORTED_PROVIDER);
    }
}
