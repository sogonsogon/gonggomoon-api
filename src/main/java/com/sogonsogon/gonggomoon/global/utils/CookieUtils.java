package com.sogonsogon.gonggomoon.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

/**
 * 쿠키 생성, 삭제, 직렬화, 역직렬화를 담당하는 글로벌 유틸리티 클래스
 */
public class CookieUtils {

    // 유틸리티 클래스는 인스턴스화할 필요가 없으므로 private 생성자로 막아둡니다. (SonarQube 등 코드 품질 도구 통과를 위함)
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

    // 2. 쿠키 생성
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // XSS 공격을 막기 위해 자바스크립트에서 쿠키에 접근하지 못하도록 설정 (매우 중요!)
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 3. 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 수명을 0으로 만들어서 브라우저가 삭제하게 유도
                    response.addCookie(cookie);
                }
            }
        }
    }

    // 4. 자바 객체를 문자열(Base64)로 직렬화 (쿠키에 담기 위함)
    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
            .encodeToString(SerializationUtils.serialize(object));
    }

    // 5. 문자열(Base64)을 다시 자바 객체로 역직렬화 (쿠키에서 꺼내 쓰기 위함)
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
            Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
