package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 공고 URL 분석 작업을 AI 워커(/tasks/execute)로 전달하기 위한 메시지 바디.
 * 워커는 job_type으로 작업을 분기하고, 최상위 id(post ID)로 DB에서 공고를 조회해 분석한 뒤
 * callback_url로 결과를 회신한다. id는 콜백의 최상위 id로 에코백된다.
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

        @JsonProperty("post_id")
        Long postId,

        @JsonProperty("file_asset_id")
        Long fileAssetId
) {
}
