package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 경험 추출 작업을 AI 워커(/tasks/execute)로 전달하기 위한 메시지 바디.
 * 워커는 job_type으로 작업을 분기하고, file_asset_ids로 S3 파일을 내려받아 처리한 뒤
 * callback_url로 결과를 회신한다.
 */
public record ExperienceExtractionAiServerRequest(
    @JsonProperty("job_type")
    String jobType,

    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("callback_url")
    String callbackUrl,

    @JsonProperty("file_asset_ids")
    List<FileAssetTarget> fileAssetIds
) {
    /**
     * 워커가 다운로드할 파일(file_asset_id)과 결과를 매핑할 추출 작업(extracted_experience_id)의 쌍.
     */
    public record FileAssetTarget(
        @JsonProperty("file_asset_id")
        Long fileAssetId,

        @JsonProperty("extracted_experience_id")
        Long extractedExperienceId
    ) {
    }
}
