package com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2;

import com.sogonsogon.gonggomoon.domain.auth.application.TokenService;
import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthAccount;
import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthAccountRepository;
import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthProvider;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.TokenCookieManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.security.oauth2.client.redirect-front-uri}")
    private String REDIRECT_URI;

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final TokenService tokenService;
    private final OAuthAccountRepository oauthAccountRepository;

    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final TokenCookieManager tokenCookieManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        /*
        * OAuthAccount에 토큰을 저장하기 위한 로직
        * */
        // 1. AuthorizedClient를 통해 Refresh Token이 포함된 객체 획득
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
            oauthToken.getAuthorizedClientRegistrationId(),
            oauthToken.getName());

        // 2. Refresh Token 추출
        String providerAccessToken = client.getAccessToken().getTokenValue();
        String providerRefreshToken = (client.getRefreshToken() != null)
            ? client.getRefreshToken().getTokenValue()
            : null;

        AccessUser accessUser = (AccessUser) authentication.getPrincipal();
        OAuthProvider provider = OAuthProvider.from(oauthToken.getAuthorizedClientRegistrationId());

        OAuthAccount oauthAccount = oauthAccountRepository
            .findByUserIdAndProvider(accessUser.getId(), provider)
            .map(existingAccount -> {
                existingAccount.updateToken(
                    accessUser.getProviderId(),
                    providerAccessToken,
                    providerRefreshToken
                );
                return existingAccount;
            })
            .orElseGet(() -> OAuthAccount.builder()
                .userId(accessUser.getId())
                .provider(provider)
                .providerId(accessUser.getProviderId())
                .accessToken(providerAccessToken)
                .refreshToken(providerRefreshToken)
                .build());

        oauthAccountRepository.save(oauthAccount);


        /*
        * 유저에게 서비스(공고문)에서 발급하는 토큰 제공
        * */
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        // DB에 Refresh Token 저장 (로그아웃 시 검증 및 폐기 위해)
        tokenService.issueRefreshToken(accessUser.getId(), refreshToken);

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