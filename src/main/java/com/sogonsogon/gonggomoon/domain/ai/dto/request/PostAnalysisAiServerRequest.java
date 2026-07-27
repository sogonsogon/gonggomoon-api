package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 공고 URL 분석 작업을 AI 워커(/tasks/execute)로 전달하기 위한 메시지 바디.
 * 워커는 job_type으로 작업을 분기하고, S3에 업로드된 공고 원문(Tavily rawContent)을
 * file_asset_id로 내려받아 분석한 뒤 callback_url로 결과를 회신한다.
 * id(post ID)는 콜백의 최상위 id로 에코백된다.
 */
public record PostAnalysisAiServerRequest(
        @JsonProperty("id")
        Long id,

        @JsonProperty("job_type")
        String jobType,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("callback_url")
        String callbackUrl,

        @JsonProperty("file_asset_id")
        Long fileAssetId
) {
}
