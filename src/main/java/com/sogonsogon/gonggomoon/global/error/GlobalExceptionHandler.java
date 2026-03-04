package com.sogonsogon.gonggomoon.global.error;

import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j // 로깅을 위한 어노테이션 추가
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 우리가 만든 BaseException 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
        String traceId = MDC.get("traceId");
        BaseErrorCode errorCode = e.getErrorCode();

        // 비즈니스 예외도 추적을 위해 로그를 남깁니다 (Warn 레벨 권장)
        log.warn("[BaseException] traceId={}, code={}, message={}", traceId, errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    // 2. 예상하지 못한 에러 처리 (가장 중요 ⭐)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        String traceId = MDC.get("traceId");

        // e.printStackTrace() 대신 log.error 사용 (Loki로 스택 트레이스 전체 전송)
        log.error("[UnhandledException] traceId={}, message={}", traceId, e.getMessage(), e);

        GlobalErrorCode error = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
            .status(error.getStatus())
            .body(BaseResponse.fail(error.getCode(), error.getMessage()));
    }

    // 3. @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String traceId = MDC.get("traceId");

        List<BaseResponse.ValidationError> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> BaseResponse.ValidationError.builder()
                .field(fe.getField())
                .reason(fe.getDefaultMessage())
                .build()
            )
            .toList();

        log.warn("[MethodArgumentNotValidException] traceId={}, errors={}", traceId, errors);

        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.name(), errorCode.getMessage(), errors /*, traceId*/));
    }
}
