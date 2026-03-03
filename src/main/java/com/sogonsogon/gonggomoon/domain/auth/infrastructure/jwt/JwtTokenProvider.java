package com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_TYPE_KEY = "typ";
    private static final String ACCESS_TYPE = "access";
    private static final String REFRESH_TYPE = "refresh";

    private final Key key;

    // access 만료(ms)
    private final long accessTokenValidityMs;

    // refresh 만료(ms)
    private final long refreshTokenValidityMs;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKey,
        @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValiditySeconds,
        @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValiditySeconds
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.accessTokenValidityMs = accessTokenValiditySeconds * 1000;
        this.refreshTokenValidityMs = refreshTokenValiditySeconds * 1000;
    }

    /**
     * ✅ Access Token 생성: 권한 포함, 만료 짧게
     */
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiry = new Date(now + accessTokenValidityMs);

        return Jwts.builder()
            .subject(authentication.getName())
            .issuedAt(issuedAt)
            .expiration(expiry)
            .claim(AUTHORITIES_KEY, authorities)
            .claim(TOKEN_TYPE_KEY, ACCESS_TYPE)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * ✅ Refresh Token 생성: 최소 정보만, 만료 길게
     * - 권한(auth) 같은 건 안 넣는 걸 추천
     * - typ=refresh 같은 식별용 claim만 둠
     */
    public String createRefreshToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiry = new Date(now + refreshTokenValidityMs);

        return Jwts.builder()
            .subject(authentication.getName())
            .issuedAt(issuedAt)
            .expiration(expiry)
            .claim(TOKEN_TYPE_KEY, REFRESH_TYPE)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Access 토큰 기준 인증 객체 생성
     * - refresh로 들어오면 auth가 없어서 여기서 터질 수 있음
     * - refresh는 별도 검증/재발급 API에서만 쓰는 걸 권장
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        Object authClaim = claims.get(AUTHORITIES_KEY);
        if (authClaim == null) {
            // refreshToken을 여기로 넣는 실수를 막기 위해 명확히 예외 처리
            throw new IllegalArgumentException("권한 정보(auth)가 없는 토큰입니다. (refresh token일 수 있음)");
        }

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(authClaim.toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        org.springframework.security.core.userdetails.User principal =
            new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰 타입(access/refresh) 확인이 필요할 때 사용
     */
    public String getTokenType(String token) {
        Claims claims = Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        Object typ = claims.get(TOKEN_TYPE_KEY);
        return typ == null ? null : typ.toString();
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TYPE.equals(getTokenType(token));
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TYPE.equals(getTokenType(token));
    }
}