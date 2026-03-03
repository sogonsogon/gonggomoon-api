package com.sogonsogon.gonggomoon.domain.auth.infrastructure.security;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Request Header에서 JWT 토큰을 꺼냅니다. (보통 "Authorization: Bearer <token>" 형태)
        String jwt = getJwtFromRequest(request);

        // 2. 토큰이 존재하고, 유효한 서명/만료일자를 가졌는지 검증합니다.
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            // 3. 유효하다면 토큰에서 유저 정보(ID나 권한)를 뽑아 Authentication 객체를 만듭니다.
            Authentication authentication = tokenProvider.getAuthentication(jwt);

            // 4. SecurityContext에 찔러 넣습니다. 이제 컨트롤러에서 @AuthenticationPrincipal로 유저를 꺼내 쓸 수 있습니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청을 넘깁니다.
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
