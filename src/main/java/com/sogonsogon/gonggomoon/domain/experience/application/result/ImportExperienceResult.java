package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import lombok.Builder;

@Builder
public record ImportExperienceResult(
        Long fileAssetId
) {
    public static ImportExperienceResult from (FileAsset fileAsset) {
        return ImportExperienceResult.builder()
                .fileAssetId(fileAsset.getId())
                .build();
    }
}
