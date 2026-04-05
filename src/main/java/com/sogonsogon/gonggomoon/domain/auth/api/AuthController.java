package com.sogonsogon.gonggomoon.domain.auth.api;

import com.sogonsogon.gonggomoon.domain.auth.api.dto.ReissuanceResponse;
import com.sogonsogon.gonggomoon.domain.auth.application.AuthService;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.TokenCookieManager;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenCookieManager tokenCookieManager;

    // TODO : 프론트에서 로그아웃시 브라우저에 쿠키를 삭제하도록 할건지 확인
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
        @AuthenticationPrincipal AccessUser user,
        @CookieValue(name = "refresh_token") String refreshToken
        ) {
        authService.logout(user.getId(), refreshToken);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(BaseResponse.success());
    }

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<ReissuanceResponse>> reissueToken(@CookieValue(name = "refresh_token") String refreshToken) {

        ReissuanceResponse response = authService.reissueToken(refreshToken);

        ResponseCookie refreshTokenCookie = tokenCookieManager.getRefreshTokenCookie(response.refreshToken());
        ResponseCookie accessTokenCookie = tokenCookieManager.getAccessTokenCookie(response.accessToken());

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(BaseResponse.success(null));
    }
}
