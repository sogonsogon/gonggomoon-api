package com.sogonsogon.gonggomoon.domain.file.application.result;

import com.sogonsogon.gonggomoon.domain.file.domain.FileAsset;
import lombok.Builder;

@Builder
public record UploadFileResult(
        Long fileAssetId
) {
    public static UploadFileResult from (FileAsset fileAsset) {
        return UploadFileResult.builder()
                .fileAssetId(fileAsset.getId())
                .build();
    }
}
