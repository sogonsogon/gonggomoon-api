package com.sogonsogon.gonggomoon.domain.auth.infrastructure.security;

import com.sogonsogon.gonggomoon.domain.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

// 💡 실무 팁: UserDetails와 OAuth2User를 모두 구현하면 일반 로그인과 소셜 로그인을 통합 처리할 수 있습니다.
public class UserPrincipal implements OAuth2User, UserDetails, AccessUser {

    private User user;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes; // 소셜 로그인 시 구글/카카오에서 받은 원본 데이터

    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    // 💡 팩토리 메서드: 우리 DB의 User 엔티티를 받아서 UserPrincipal로 변환
    public static UserPrincipal create(User user) {
        // 권한 설정 (TODO : `ROLE_`을 붙이는 이유는 Spring Security의 관례이다.)
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()));

        return new UserPrincipal(
            user,
            authorities
        );
    }

    // 💡 소셜 로그인(OAuth2) 시 사용되는 생성 메서드 오버로딩
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    // --- AccessUser 인터페이스 구현 ---

    @Override
    public Long getId() {
        return user.getId();
    }

    @Override
    public String getProviderId() {
        // TODO : 네이버 전용 로직, 추후에 변경되어야 함
        // 1. attributes에서 "response" 객체를 Map으로 캐스팅해서 꺼냅니다.
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        // 2. 그 안에서 네이버의 고유 식별자인 "id"를 꺼내어 String으로 반환합니다.
        return String.valueOf(response.get("id")); // 소셜 로그인 시 제공되는 고유 식별자 (예: 구글의 sub, 네이버의 id)
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    // --- OAuth2User 인터페이스 구현 ---
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() { // 식별자 반환
        return String.valueOf(user.getPublicId());
    }

    // --- UserDetails 인터페이스 구현 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // 패스워드는 저장하지 않음 !
    }

    @Override
    public String getUsername() {
        return String.valueOf(user.getPublicId());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}