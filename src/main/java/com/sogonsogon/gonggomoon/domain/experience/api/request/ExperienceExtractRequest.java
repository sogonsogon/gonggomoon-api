package com.sogonsogon.gonggomoon.domain.experience.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ExperienceExtractRequest(
        @NotNull(message = "fileAssetIds는 필수입니다.")
        List<@NotNull(message = "fileAssetId는 null일 수 없습니다.") Long> fileAssetIds
) {
}
