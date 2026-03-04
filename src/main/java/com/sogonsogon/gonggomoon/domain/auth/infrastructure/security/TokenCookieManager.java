package com.sogonsogon.gonggomoon.domain.auth.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component // Bean으로 등록이 되어있어야 @Value를 사용할 수 있다.
public class TokenCookieManager {

    // Cookie 관련 설정 값
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.http-only}")
    private boolean cookieHttpOnly;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;


    /**
     * Refresh Token을 쿠키로 저장하는 메서드입니다.
     * */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
            .path("/")
            .maxAge(60 * 60 * 24 * 14) // 14일
            .httpOnly(cookieHttpOnly)
            .secure(cookieSecure)      // 동적 할당
            .sameSite(cookieSameSite)  // 동적 할당
            .build();

        // 문자열 조립 대신 HttpHeaders.SET_COOKIE 상수와 ResponseCookie 객체 사용
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Access Token을 쿠키로 저장하는 메서드입니다.
     * */
    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
            .path("/")
            .maxAge(60 * 30) // 30분
            .httpOnly(cookieHttpOnly)
            .secure(cookieSecure)      // 동적 할당
            .sameSite(cookieSameSite)  // 동적 할당
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
