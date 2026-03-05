package com.sogonsogon.gonggomoon.domain.auth.api;

import com.sogonsogon.gonggomoon.domain.auth.application.AuthService;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
