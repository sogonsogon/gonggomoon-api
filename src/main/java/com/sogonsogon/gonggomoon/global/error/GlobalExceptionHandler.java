package com.sogonsogon.gonggomoon.global.error;

import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 우리가 만든 BaseException 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {

        BaseErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    // 2. 예상하지 못한 에러 처리 (가장 중요 ⭐)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {

        GlobalErrorCode error = GlobalErrorCode.INTERNAL_SERVER_ERROR;

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

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage(), errors));
    }
}
