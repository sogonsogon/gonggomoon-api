package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sogonsogon.gonggomoon.global.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    // TODO : 아래 메서드로 테스트 해보고 필요 없으면 삭제
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//
//        // 1. 프론트엔드가 처음에 지정했던 redirect_uri를 쿠키에서 꺼냅니다.
//        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
//            .map(Cookie::getValue);
//
//        // 2. JWT(Access Token)를 생성합니다.
//        String token = tokenProvider.createToken(authentication);
//
//        // 3. 최종적으로 이동할 프론트엔드 URL을 조립합니다. (예: http://localhost:3000/oauth2/redirect?token=xxxx)
//        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri.orElse(getDefaultTargetUrl()))
//            .queryParam("token", token)
//            .build().toUriString();
//
//        // 4. 인증 과정에서 썼던 임시 쿠키들을 싹 청소합니다.
//        clearAuthenticationAttributes(request, response);
//
//        // 5. 프론트엔드로 리다이렉트!
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
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
    // TODO : 프론트 리다이렉트 주소 받아와야 할 듯 ? -> 그리고 해당 callback 주소에서 /api/v1/users/me API를 호출하도록 해야함 !
    String targetUrl = "http://localhost:3000/auth/callback";
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