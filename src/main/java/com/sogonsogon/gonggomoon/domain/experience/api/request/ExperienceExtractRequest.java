package com.sogonsogon.gonggomoon.domain.experience.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ExperienceExtractRequest(
        @Schema(description = "경험 추출에 사용할 파일 자산 ID 목록 (최대 2개, 중복 불가)", example = "[101, 102]", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "fileAssetIds는 필수입니다.")
        List<@NotNull(message = "fileAssetId는 null일 수 없습니다.") Long> fileAssetIds
) {
}
