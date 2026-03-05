package com.sogonsogon.gonggomoon.domain.auth.application;

import com.sogonsogon.gonggomoon.domain.auth.application.exception.OAuthUnlinkFailException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    public void unlinkNaver(String accessToken) {
        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete" +
            "&client_id=" + clientId +
            "&client_secret=" + clientSecret +
            "&access_token=" + accessToken +
            "&service_provider=NAVER";

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && "success".equals(body.get("result"))) {
                // 성공적으로 해제됨
                log.info("네이버 연동 해제 성공");
            } else {
                // 실패 처리 (에러 코드 확인 필요)
                log.error("네이버 연동 해제 실패: {}", body);
                throw new OAuthUnlinkFailException();
            }
        } catch (Exception e) {
            log.error("네이버 API 호출 중 오류 발생", e);
            throw new OAuthUnlinkFailException();
        }
    }
}