package com.sogonsogon.gonggomoon.domain.user.application;

import com.sogonsogon.gonggomoon.domain.auth.application.NaverOAuthService;
import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthAccountRepository;
import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthProvider;
import com.sogonsogon.gonggomoon.domain.user.application.exception.UserNotFoundException;
import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final NaverOAuthService naverOAuthService;

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    /*
    * 사용자: 탈퇴 버튼 클릭
    * 우리 서버: DB에서 해당 사용자의 naver_access_token을 조회 (만료되었다면 refresh_token으로 갱신 필요)
    * 우리 서버 → 네이버: grant_type=delete와 함께 토큰 전송
    * 네이버 → 우리 서버: 결과 응답 (result: success)
    * 우리 서버: 로컬 DB 데이터 삭제/업데이트 및 세션 종료
    * */
    @Transactional
    public void withdrawUser(Long userId) {
        // NOTE : 사용자 존재 여부 체크
        User findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // NOTE : 사용자 탈퇴 시, 네이버 연동 해제
        oAuthAccountRepository.findByUserId(userId).ifPresent(account -> {
            if (account.getProvider() == OAuthProvider.NAVER) {
                naverOAuthService.unlinkNaver(account.getAccessToken());
            }
        });

        // 사용자 정보 삭제 (또는 상태 업데이트)
        oAuthAccountRepository.deleteByUserId(userId);
        userRepository.delete(findUser);
    }
}
