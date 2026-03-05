package com.sogonsogon.gonggomoon.global.error;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    String getCode(); // 내부 식별용 (예: "USER_NOT_FOUND")
    HttpStatus getStatus(); // HTTP 상태 코드 (예: HttpStatus.NOT_FOUND)
    String getMessage(); // 사용자에게 보여줄 메시지 (예: "사용자를 찾을 수 없습니다.")
}
