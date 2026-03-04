package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.HttpCookieOAuth2AuthorizationRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.security.oauth2.client.redirect-front-uri}")
    private String REDIRECT_URI;

    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

@Override
public void onAuthenticationSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {

    String accessToken = tokenProvider.createAccessToken(authentication);
    String refreshToken = tokenProvider.createRefreshToken(authentication);

    // ✅ 1) refresh는 HttpOnly 쿠키로
    addRefreshTokenCookie(response, refreshToken);

    // ✅ 2) access는 노출 최소화: HttpOnly 쿠키로 같이 넣거나(선택) / 안 넣고 이후 /auth/token으로 발급
    // 선택 A: access도 HttpOnly 쿠키로 저장 (SSR/CSR 모두 편함)
    addAccessTokenCookie(response, accessToken);

    clearAuthenticationAttributes(request, response);

    // ✅ 3) 프론트 콜백으로만 리다이렉트 (token 파라미터 없음)
    // TODO : 프론트 리다이렉트 주소 받아와야 할 듯 ? -> application.yml에서 수정하면 됨.
    String targetUrl = REDIRECT_URI;
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
}

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        // 로컬(http)에서는 secure=false, 배포(https)에서는 true
        boolean secure = false;

        // SameSite는 Servlet Cookie API로 직접 설정이 어려워서 Set-Cookie 헤더로 세팅하는 게 깔끔
        String cookie = String.format(
            "refresh_token=%s; Path=/; Max-Age=%d; HttpOnly; %s SameSite=Lax",
            refreshToken,
            60 * 60 * 24 * 14,
            secure ? "Secure;" : ""
        );

        response.addHeader("Set-Cookie", cookie);
    }

    private void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        boolean secure = false;

        String cookie = String.format(
            "access_token=%s; Path=/; Max-Age=%d; HttpOnly; %s SameSite=Lax",
            accessToken,
            60 * 30, // 30분 예시
            secure ? "Secure;" : ""
        );

        response.addHeader("Set-Cookie", cookie);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}