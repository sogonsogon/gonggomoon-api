package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;


import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    // 💡 네이버의 실제 유저 정보가 담긴 안쪽 Map을 캐싱해 둡니다.
    private Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);

        // 1. "response" 키가 존재하는지, 데이터가 있는지 확인
        Object responseObj = attributes.get("response");

        // TODO : 추후에 CustomException이 추가되면 IllegalArgumentException 대신 해당 예외로 변경되어야 할 로직
        if (responseObj == null) {
            // 로깅을 함께 남겨주면 추후 디버깅에 매우 유리합니다.
            throw new IllegalArgumentException("네이버 로그인 응답에 'response' 필드가 존재하지 않습니다. 네이버 API 응답 구조가 변경되었는지 확인하세요. attributes: " + attributes);
        }

        // 2. "response" 데이터가 우리가 기대하는 Map 형식이 맞는지 확인
        // TODO : 추후에 CustomException이 추가되면 IllegalArgumentException 대신 해당 예외로 변경되어야 할 로직
        if (!(responseObj instanceof Map)) {
            throw new IllegalArgumentException("네이버 로그인 응답의 'response' 필드가 Map 형식이 아닙니다. API 응답 구조가 변경되었는지 확인하세요. 실제 타입: " + responseObj.getClass().getName());
        }

        // 3. 안전하게 캐스팅
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) responseObj;
        this.response = responseMap;
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