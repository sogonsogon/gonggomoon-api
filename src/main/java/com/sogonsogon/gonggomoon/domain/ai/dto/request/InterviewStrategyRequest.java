package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 면접 전략 생성 작업을 AI 워커(/tasks/execute)로 전달하기 위한 메시지 바디.
 * 워커는 최상위 id(면접 전략 ID)로 DB에서 파일을 조회해 처리하며,
 * id는 콜백의 최상위 id로 에코백된다.
 */
public record InterviewStrategyRequest(
    @JsonProperty("id")
    Long id,

    @JsonProperty("job_type")
    String jobType,

    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("callback_url")
    String callbackUrl
) {
}
