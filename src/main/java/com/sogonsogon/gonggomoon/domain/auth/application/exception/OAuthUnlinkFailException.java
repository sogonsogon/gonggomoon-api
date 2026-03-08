package com.sogonsogon.gonggomoon.domain.auth.application.exception;

import com.sogonsogon.gonggomoon.global.error.BaseException;

public class OAuthUnlinkFailException extends BaseException {
    public OAuthUnlinkFailException() {
        super(AuthErrorCode.OAUTH_UNLINK_FAIL);
    }
}
