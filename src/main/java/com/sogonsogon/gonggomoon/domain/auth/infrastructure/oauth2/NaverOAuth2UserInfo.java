package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;


import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    // 💡 네이버의 실제 유저 정보가 담긴 안쪽 Map을 캐싱해 둡니다.
    private Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        // 네이버는 attributes.get("response") 안에 진짜 정보가 있습니다.
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) response.get("id");
    }

    @Override
    public String getName() {
        return (String) response.get("name");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) response.get("profile_image");
    }

    // --- 💡 새로 추가하신 항목들을 꺼내는 메서드 ---

    public String getGender() {
        return (String) response.get("gender"); // F: 여성, M: 남성, U: 확인불가
    }

    public String getBirthday() {
        return (String) response.get("birthday"); // MM-DD
    }

    public String getBirthyear() {
        return (String) response.get("birthyear"); // YYYY
    }
}