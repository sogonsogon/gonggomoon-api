package com.sogonsogon.gonggomoon.domain.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * JWT 인증 실패 시 (토큰 없음 / 만료) OAuth2 로그인 진입점으로 리다이렉트하는 기본 동작을 막고,
 * 401 Unauthorized JSON 응답을 반환하는 EntryPoint.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String ERROR_CODE = "AUTH_UNAUTHORIZED";
    private static final String ERROR_MESSAGE = "인증이 필요합니다. 액세스 토큰을 확인해 주세요.";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        BaseResponse<?> body = BaseResponse.fail(ERROR_CODE, ERROR_MESSAGE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

