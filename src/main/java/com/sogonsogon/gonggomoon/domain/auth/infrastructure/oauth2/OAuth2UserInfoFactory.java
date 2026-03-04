package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        // NOTE : OAuth가 추가되면 여기서 분기 처리 해주면 됩니다.
        if (registrationId.equalsIgnoreCase("naver")) {
            return new NaverOAuth2UserInfo(attributes);
        }

        // TODO : CustomException이 추가 되면 변경되어야 할 로직
        throw new IllegalArgumentException("Unsupported Login Type : " + registrationId);
    }
}