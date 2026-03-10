package com.sogonsogon.gonggomoon.global.config;

import com.sogonsogon.gonggomoon.domain.auth.application.CustomOAuth2UserService;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2.OAuth2SuccessHandler;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 💡 핵심: 세션 대신 쿠키에 OAuth2 인증 상태를 저장하는 커스텀 클래스
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. REST API 규격에 맞는 기본 설정 비활성화
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)

            // 2. JWT를 사용하므로 세션 관리 정책을 STATELESS로 설정
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 3. OAuth2 로그인 필터 설정
            .oauth2Login(oauth2 -> oauth2
                // [검문소 1] 프론트가 호출할 진입점 (설정하신 엔드포인트 적용)
                .authorizationEndpoint(auth -> auth
                    // 예시) http://localhost:8080/api/v1/auth/social/login/naver를 호출하면 네이버 Oauth2 로그인 프로세스가 시작됩니다.
                    .baseUri("/api/v1/auth/social/login")
                    // 세션을 못 쓰니 쿠키를 임시 저장소로 사용하겠다고 선언!
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                )
                // [검문소 2] 구글에서 인증을 마치고 리다이렉트 되어 돌아오는 기본 주소는 application에 정의합니다
                // 예시) http://localhost:8080/login/oauth2/code/naver로 네이버에서 리다이렉트가 오면, 이 주소가 스프링 시큐리티의 OAuth2 로그인 프로세스에 걸립니다.
                // 이 주소로 코드가 오면, 아래의 UserService가 작동하여 Provider에서 유저 정보를 가져옵니다.
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // DB에 유저 정보 저장 및 업데이트 로직
                )
                // [검문소 3] 모든 인증이 성공했을 때 JWT를 발급하고 프론트로 넘겨줄 핸들러
                .successHandler(oAuth2SuccessHandler)
            )

            // 4. API 경로별 인가(권한) 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll() // 헬스체크는 모두 허용
                .requestMatchers("/api/v1/auth/social/login/**", "/oauth2/**").permitAll() // 인증 진입점은 모두 허용
                .requestMatchers("/api/v1/auth/reissue").permitAll()
                .anyRequest().authenticated() // 나머지는 JWT 인증 필요
            )

            // 5. 일반적인 API 요청을 처리할 JWT 커스텀 필터 등록
            // 스프링의 기본 인증 필터(UsernamePassword)가 돌기 전에 우리 JWT 필터를 먼저 거치도록 설정
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}