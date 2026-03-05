package com.sogonsogon.gonggomoon.domain.auth.application;

import com.sogonsogon.gonggomoon.domain.auth.api.dto.ReissuanceResponse;
import com.sogonsogon.gonggomoon.domain.auth.application.exception.RefreshTokenInvalidException;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.sogonsogon.gonggomoon.domain.user.application.exception.UserNotFoundException;
import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.domain.user.domain.UserRepository;
import io.jsonwebtoken.Claims;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final UserRepository userRepository;


    @Transactional
    public void logout(Long userId, String refreshToken) {

        if (refreshToken == null
            || !jwtTokenProvider.validateToken(refreshToken)
            || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RefreshTokenInvalidException();
        }

        tokenService.revokeRefreshToken(userId, refreshToken);
    }

    // TODO : DTO 패키지를 도메인 하위로 두는 것을 고민 !
    @Transactional
    public ReissuanceResponse reissueToken(String refreshToken) {
        // 1. 리프레시 토큰 유효성 및 Null 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new RefreshTokenInvalidException();
        }

        // 2. 토큰에서 사용자 정보(public_id) 추출
        Claims claims = jwtTokenProvider.parseToken(refreshToken);
        String publicId = claims.getSubject(); // 변수명 컨벤션 수정 (스네이크 -> 카멜)

        // 3. 유저 정보 조회
        User findUser = userRepository.findByPublicId(UUID.fromString(publicId))
            .orElseThrow(UserNotFoundException::new);

        // 4. 권한(GrantedAuthority) 생성
        // 실무에서는 Role에 보통 "ROLE_" 접두사를 붙여 Spring Security 표준을 맞춥니다.
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + findUser.getRole().name())
        );

        // 5. Authentication 객체 생성
        // principal에는 publicId(String), credentials는 빈 문자열, authorities는 위에서 만든 권한 리스트를 넣습니다.
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            findUser.getPublicId().toString(),
            "",
            authorities
        );

        // 6. 새로운 액세스 토큰 & 리프레시 토큰 발급 (RTR 방식)
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        // 참고: createRefreshToken도 authentication이나 publicId를 받도록 시그니처를 맞추는 것이 좋습니다.
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 7. 새로 발급한 refresh token 저장소 업데이트 로직 (RTR 방식)
        tokenService.rotateRefreshToken(findUser.getId(), refreshToken, newRefreshToken);

        return new ReissuanceResponse("Bearer", newAccessToken, newRefreshToken);
    }
}
