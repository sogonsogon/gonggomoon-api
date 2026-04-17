package com.sogonsogon.gonggomoon.global.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 우리가 만든 BaseException 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {

        BaseErrorCode errorCode = e.getErrorCode();

        log.warn(
            "BaseException occurred. code={}, message={}, exceptionMessage={}",
            errorCode.getCode(),
            errorCode.getMessage(),
            e.getMessage(),
            e
        );

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    // 2. 예상하지 못한 에러 처리 (가장 중요 ⭐)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {

        GlobalErrorCode error = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        log.error("Unhandled exception occurred", e);

        return ResponseEntity
            .status(error.getStatus())
            .body(BaseResponse.fail(error.getCode(), error.getMessage()));
    }

    // 3. @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        List<BaseResponse.ValidationError> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> BaseResponse.ValidationError.builder()
                .field(fe.getField())
                .reason(fe.getDefaultMessage())
                .build()
            )
            .toList();

        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        log.warn("Validation failed. errors={}", errors, e);

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage(), errors));
    }

    // 4. enum 바인딩 실패 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {

        Throwable cause = e.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().isEmpty()
                    ? null
                    : invalidFormatException.getPath().get(0).getFieldName();

            Class<?> targetType = invalidFormatException.getTargetType();

            if ("jobType".equals(fieldName) && targetType == JobType.class) {
                log.warn("Invalid enum value for jobType. value={}", invalidFormatException.getValue(), e);

                return ResponseEntity
                        .status(PortfolioStrategyErrorCode.INVALID_JOB_TYPE.getStatus())
                        .body(BaseResponse.fail(
                                PortfolioStrategyErrorCode.INVALID_JOB_TYPE.getCode(),
                                PortfolioStrategyErrorCode.INVALID_JOB_TYPE.getMessage()
                        ));
            }
        }

        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        log.warn("Http message not readable", e);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<BaseResponse<?>> handleMissingRequestCookie(MissingRequestCookieException e) {
        GlobalErrorCode errorCode = GlobalErrorCode.MISSING_REQUEST_COOKIE;

        log.warn("Missing request cookie. cookieName={}", e.getCookieName(), e);

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }
}
