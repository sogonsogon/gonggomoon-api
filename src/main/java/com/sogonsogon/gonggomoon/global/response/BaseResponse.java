package com.sogonsogon.gonggomoon.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE) // 빌더 패턴을 만들어주는 어노테이션, 빌더를 외부에서 사용하지 못하게 하기 위해서 접근 레벨을 PRIVATE로 설정
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드를 매개변수로 받는 생성자를 만들어주는 어노테이션, 생성자를 외부에서 사용하지 못하게 하기 위해서 접근 레벨을 PRIVATE로 설정
@JsonInclude(JsonInclude.Include.NON_NULL) // JSON 직렬화 시 null인 필드는 포함하지 않도록 설정
public class BaseResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final List<FieldError> errors;
    private final PageInfo pageInfo;
    private final LocalDateTime timestamp;

    // ──────────────────────────────────────
    // 성공 응답
    // ──────────────────────────────────────

    public static BaseResponse<Void> success() {
        return BaseResponse.<Void>builder()
                .success(true)
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return BaseResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ──────────────────────────────────────
    // 페이지네이션 응답
    // ──────────────────────────────────────

    public static <T> BaseResponse<List<T>> success(List<T> data, PageInfo pageInfo) {
        return BaseResponse.<List<T>>builder()
                .success(true)
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .pageInfo(pageInfo)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ──────────────────────────────────────
    // 실패 응답
    // ──────────────────────────────────────

    public static BaseResponse<Void> fail(String code, String message) {
        return BaseResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static BaseResponse<Void> fail(String code, String message, List<FieldError> errors) {
        return BaseResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ──────────────────────────────────────
    // 내부 DTO
    // ──────────────────────────────────────

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldError {
        private final String field;
        private final String reason;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PageInfo {
        private final int currentPage;
        private final int totalPages;
        private final long totalElements;
        private final boolean hasNext;
    }
}
