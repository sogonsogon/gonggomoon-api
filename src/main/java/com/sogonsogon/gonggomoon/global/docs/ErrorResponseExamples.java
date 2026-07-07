package com.sogonsogon.gonggomoon.global.docs;

/**
 * Swagger(OpenAPI) 문서에서 공통으로 사용하는 에러 응답 예시 모음.
 *
 * <p>{@code BaseResponse} 에러 응답 형태(success/code/message/(errors)/timestamp)를 그대로 따르며,
 * 각 상수는 {@code @ExampleObject(value = ...)} 에서 참조한다.
 * 같은 HTTP 상태에 여러 에러 코드가 존재하는 경우, 대표 예시 하나만 두고 나머지는 @ApiResponse 의 description 에 나열한다.
 */
public final class ErrorResponseExamples {

    private ErrorResponseExamples() {
    }

    // ===== 공통 (Global) =====
    public static final String VALIDATION = """
            {
              "success": false,
              "code": "GLOBAL_INVALID_INPUT_VALUE",
              "message": "잘못된 입력 값입니다.",
              "errors": [
                { "field": "title", "reason": "제목은 필수입니다." }
              ],
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "code": "GLOBAL_INTERNAL_SERVER_ERROR",
              "message": "서버 내부 오류가 발생했습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    // ===== 경험 (Experience) =====
    public static final String EXPERIENCE_INVALID_DATE_RANGE = """
            {
              "success": false,
              "code": "EXPERIENCE_INVALID_DATE_RANGE",
              "message": "종료일이 시작일보다 이전일 수 없습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String EXPERIENCE_NOT_FOUND = """
            {
              "success": false,
              "code": "EXPERIENCE_NOT_FOUND",
              "message": "해당 경험을 찾을 수 없습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String EXPERIENCE_INVALID_FILE_ASSET_REQUEST = """
            {
              "success": false,
              "code": "EXPERIENCE_INVALID_FILE_ASSET_REQUEST",
              "message": "요청한 파일 중 존재하지 않거나 본인 소유가 아닌 파일이 포함되어 있습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String EXPERIENCE_WEEKLY_LIMIT_EXCEEDED = """
            {
              "success": false,
              "code": "EXPERIENCE_WEEKLY_LIMIT_EXCEEDED",
              "message": "이번 주 경험 추출 가능 횟수를 모두 사용했습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    // ===== 추출된 경험 (Extracted Experience) =====
    public static final String EXTRACTED_EXPERIENCE_NOT_FOUND = """
            {
              "success": false,
              "code": "EXTRACTED_EXPERIENCE_NOT_FOUND",
              "message": "추출된 경험을 찾을 수 없습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    // ===== 사용자 / 인증 (User / Auth) =====
    public static final String USER_NOT_FOUND = """
            {
              "success": false,
              "code": "USER_NOT_FOUND",
              "message": "사용자를 찾을 수 없습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String AUTH_OAUTH_UNLINK_FAIL = """
            {
              "success": false,
              "code": "AUTH_OAUTH_UNLINK_FAIL",
              "message": "회원 탈퇴에 문제가 발생했습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    // ===== 포트폴리오 전략 (Portfolio Strategy) =====
    public static final String PORTFOLIO_STRATEGY_REQUESTED_EXPERIENCE_NOT_FOUND = """
            {
              "success": false,
              "code": "PORTFOLIO_STRATEGY_REQUESTED_EXPERIENCE_NOT_FOUND",
              "message": "요청한 경험 정보를 찾을 수 없습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String PORTFOLIO_STRATEGY_WEEKLY_LIMIT_EXCEEDED = """
            {
              "success": false,
              "code": "PORTFOLIO_STRATEGY_WEEKLY_LIMIT_EXCEEDED",
              "message": "이번 주 포트폴리오 전략 생성 가능 횟수를 모두 사용했습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String PORTFOLIO_STRATEGY_NOT_FOUND = """
            {
              "success": false,
              "code": "PORTFOLIO_STRATEGY_NOT_FOUND",
              "message": "해당 포트폴리오 전략을 찾을 수 없습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String PORTFOLIO_STRATEGY_RESULT_NOT_READY = """
            {
              "success": false,
              "code": "PORTFOLIO_STRATEGY_RESULT_NOT_READY",
              "message": "전략 결과가 아직 준비되지 않았습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String PORTFOLIO_STRATEGY_GENERATION_FAILED = """
            {
              "success": false,
              "code": "PORTFOLIO_STRATEGY_GENERATION_FAILED",
              "message": "포트폴리오 전략 생성에 실패했습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    // ===== 산업 (Industry) =====
    public static final String INDUSTRY_NOT_FOUND = """
            {
              "success": false,
              "code": "INDUSTRY_NOT_FOUND",
              "message": "존재하지 않는 산업입니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    // ===== AI 작업 (AI) =====
    public static final String AI_SERVER_ERROR = """
            {
              "success": false,
              "code": "AI_SERVER_ERROR",
              "message": "AI 서버에서 오류가 발생했습니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";

    public static final String AI_INVALID_TYPE = """
            {
              "success": false,
              "code": "AI_INVALID_TYPE",
              "message": "유효하지 않은 type입니다.",
              "timestamp": "2026-06-24T10:30:00Z"
            }""";
}
