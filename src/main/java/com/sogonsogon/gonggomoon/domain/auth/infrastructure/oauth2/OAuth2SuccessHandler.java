package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.TokenCookieManager;
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
    private final TokenCookieManager tokenCookieManager;

@Override
public void onAuthenticationSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {

    String accessToken = tokenProvider.createAccessToken(authentication);
    String refreshToken = tokenProvider.createRefreshToken(authentication);

    // ✅ 1) refresh는 HttpOnly 쿠키로
    tokenCookieManager.addRefreshTokenCookie(response, refreshToken);

    // ✅ 2) access는 노출 최소화: HttpOnly 쿠키로 같이 넣거나(선택) / 안 넣고 이후 /auth/token으로 발급
    // 선택 A: access도 HttpOnly 쿠키로 저장 (SSR/CSR 모두 편함)
    tokenCookieManager.addAccessTokenCookie(response, accessToken);

    clearAuthenticationAttributes(request, response);

    // ✅ 3) 프론트 콜백으로만 리다이렉트 (token 파라미터 없음)
    // TODO : 프론트 리다이렉트 주소 받아와야 할 듯 ? -> application.yml에서 수정하면 됨.
    String targetUrl = REDIRECT_URI;
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
}

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}