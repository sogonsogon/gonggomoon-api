package com.sogonsogon.gonggomoon.domain.auth.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenCookieManager {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.http-only}")
    private boolean cookieHttpOnly;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValiditySeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValiditySeconds;

    /**
     * Refresh Token을 쿠키로 저장하는 메서드입니다.
     * */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = createTokenCookie("refresh_token", refreshToken, refreshTokenValiditySeconds);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return createTokenCookie("refresh_token", refreshToken, refreshTokenValiditySeconds);
    }

    /**
     * Access Token을 쿠키로 저장하는 메서드입니다.
     * */
    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = createTokenCookie("access_token", accessToken, accessTokenValiditySeconds);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseCookie getAccessTokenCookie(String accessToken) {
        return createTokenCookie("access_token", accessToken, accessTokenValiditySeconds);
    }

    public void expireAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = createTokenCookie("access_token", "", 0);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void expireRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = createTokenCookie("refresh_token", "", 0);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void expireAllTokenCookies(HttpServletResponse response) {
        expireAccessTokenCookie(response);
        expireRefreshTokenCookie(response);
    }

    private ResponseCookie createTokenCookie(String name, String value, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
            .path("/")
            .maxAge(maxAgeSeconds)
            .httpOnly(cookieHttpOnly)
            .secure(cookieSecure)
            .sameSite(cookieSameSite);

        if (StringUtils.hasText(cookieDomain)) {
            cookieBuilder.domain(cookieDomain);
        }

        return cookieBuilder.build();
    }
}
