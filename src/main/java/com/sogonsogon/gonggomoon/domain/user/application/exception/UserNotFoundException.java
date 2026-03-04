package com.sogonsogon.gonggomoon.domain.user.application.exception;

import com.sogonsogon.gonggomoon.global.error.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
