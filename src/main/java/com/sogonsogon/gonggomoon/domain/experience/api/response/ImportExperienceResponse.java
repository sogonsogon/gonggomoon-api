package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.ImportExperienceResult;
import lombok.Builder;

@Builder
public record ImportExperienceResponse(
        Long fileAssetId
) {
    public static ImportExperienceResponse from (ImportExperienceResult result) {
        return ImportExperienceResponse.builder()
                .fileAssetId(result.fileAssetId())
                .build();
    }
}
