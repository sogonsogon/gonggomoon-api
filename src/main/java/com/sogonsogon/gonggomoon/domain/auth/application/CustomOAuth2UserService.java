package com.sogonsogon.gonggomoon.domain.auth.application;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2.OAuth2UserInfo;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.oauth2.OAuth2UserInfoFactory;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.UserPrincipal;
import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.domain.user.domain.User.Role;
import com.sogonsogon.gonggomoon.domain.user.domain.User.Status;
import com.sogonsogon.gonggomoon.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 구현체를 통해 Provider에서 유저 정보를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어떤 벤더(구글, 네이버 등)인지 확인합니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. 벤더마다 다른 JSON 구조를 하나의 공통 DTO(OAuth2UserInfo)로 규격화합니다. (팩토리 패턴 사용)
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        // 4. DB를 조회하여 없으면 회원가입(Save), 있으면 정보 갱신(Update)
        User user = userRepository.findByEmail(userInfo.getEmail())
            .map(existingUser -> updateExistingUser(existingUser, userInfo))
            .orElseGet(() -> registerNewUser(userInfo, registrationId));

        // 5. Spring Security가 이해할 수 있는 UserPrincipal 객체로 래핑해서 반환합니다.
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    /**
     * 신규 소셜 가입자 등록
     */
    private User registerNewUser(OAuth2UserInfo userInfo, String registrationId) {
        // 💡 실무 팁 2: Builder 패턴을 사용하여 가독성을 높이고 필수 값을 명확히 합니다.
        User newUser = User.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .status(Status.ACTIVE)
            // Enum 타입으로 관리하는 것을 강력히 권장합니다. (ex: AuthProvider.GOOGLE)
//            .provider(registrationId) // TODO : 프로바이더는 oauth 테이블에서 관리해야하는거 같음.
//            .providerId(userInfo.getProviderId())
            .role(Role.USER) // 시스템 기본 권한 부여 (User 엔티티 설계에 맞게 추가)
            .build();

        return userRepository.save(newUser);
    }

    /**
     * 기존 가입자 정보 갱신 (구글/카카오에서 프로필 사진이나 이름이 바뀌었을 경우 동기화)
     */
    private User updateExistingUser(User existingUser, OAuth2UserInfo userInfo) {
        // 💡 실무 팁 3: setter(setName, setImageUrl)를 남발하지 않고, 도메인 엔티티 내부에 의미 있는 메서드를 만듭니다.
//        existingUser.updateProfile(userInfo.getName(), userInfo.getImageUrl());

        // 영속성 컨텍스트(JPA) 환경에서는 @Transactional이 걸려있으므로
        // dirty checking(변경 감지)이 발생하여 굳이 save()를 호출하지 않아도 UPDATE 쿼리가 날아갑니다.
        // 하지만 직관성을 위해 명시적으로 호출해 주어도 무방합니다.
        return userRepository.save(existingUser);
    }
}
