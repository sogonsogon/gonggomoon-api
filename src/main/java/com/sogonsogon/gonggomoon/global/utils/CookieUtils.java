package com.sogonsogon.gonggomoon.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    private CookieUtils() {
        throw new IllegalStateException("Utility class");
    }

    // 1. 쿠키 조회
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    // 2. 쿠키 생성 (💡 핵심: ResponseCookie로 교체)
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .httpOnly(true)
            .maxAge(maxAge)
            // 💡 [실무 필수 설정] 타 도메인(프론트엔드)에서 쿠키를 주고받으려면 아래 두 설정이 세트로 필요합니다.
            .sameSite("None")
            .secure(true) // 🚨 주의: SameSite=None을 사용하려면 반드시 Secure=true(HTTPS)여야 합니다.
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    // 3. 쿠키 삭제 (💡 핵심: 삭제할 때도 동일한 속성으로 덮어써야 브라우저가 확실히 지웁니다)
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                        .path("/")
                        .maxAge(0)
                        .sameSite("None")
                        .secure(true)
                        .build();
                    response.addHeader("Set-Cookie", deleteCookie.toString());
                }
            }
        }
    }

    // 4. 직렬화 / 역직렬화 (기존과 동일)
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}