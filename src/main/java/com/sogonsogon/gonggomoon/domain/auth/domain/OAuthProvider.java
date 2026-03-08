package com.sogonsogon.gonggomoon.domain.auth.domain;

import com.sogonsogon.gonggomoon.domain.auth.domain.dto.AuthInvalidValue;
import com.sogonsogon.gonggomoon.domain.auth.domain.dto.AuthUnsupportedProvider;
import java.util.Locale;

public enum OAuthProvider {
    NAVER;

    /**
     * 문자열을 OAuthProvider enum으로 변환하는 팩토리 메서드입니다.
     *
     * @param value 변환할 문자열 (예: "NAVER")
     * @return 해당 문자열에 대응하는 OAuthProvider enum 값
     * @throws AuthInvalidValue : 입력된 문자열이 null이거나 빈 문자열인 경우
     * @throws AuthUnsupportedProvider : 입력된 문자열이 지원되지 않는 OAuthProvider인 경우
    * */
    public static OAuthProvider from(String value) {
        if (value == null || value.isBlank()) {
            throw new AuthInvalidValue();
        }

        try {
            // NOTE : Locale.ROOT를 사용하는 이유는 대소문자 변환이 언어에 따라 다르게 동작할 수 있기 때문입니다.
            // 예를 들어, 터키어에서는 'i'가 'I'로 변환되지 않고 'İ'로 변환됩니다.
            // Locale.ROOT를 사용하면 이러한 언어 특성에 영향을 받지 않고 일관된 결과를 얻을 수 있습니다.
            value = value.trim().toUpperCase(Locale.ROOT);
            return OAuthProvider.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new AuthUnsupportedProvider();
        }
    }
}
