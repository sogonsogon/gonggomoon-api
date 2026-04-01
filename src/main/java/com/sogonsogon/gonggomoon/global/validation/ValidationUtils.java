package com.sogonsogon.gonggomoon.global.validation;

import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;

public class ValidationUtils {
    public static void requireText(String value, BaseErrorCode baseErrorCode) {
        if (value == null || value.isBlank()) {
            throw new BaseException(baseErrorCode);
        }
    }

    public static void requireNonNull(Object value, BaseErrorCode baseErrorCode) {
        if (value == null) {
            throw new BaseException(baseErrorCode);
        }
    }
}