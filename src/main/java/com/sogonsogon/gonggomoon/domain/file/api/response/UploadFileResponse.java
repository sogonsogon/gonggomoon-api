package com.sogonsogon.gonggomoon.domain.file.api.response;

import com.sogonsogon.gonggomoon.domain.file.application.result.UploadFileResult;
import lombok.Builder;

@Builder
public record UploadFileResponse(
        Long fileAssetId
) {
    public static UploadFileResponse from (UploadFileResult result) {
        return UploadFileResponse.builder()
                .fileAssetId(result.fileAssetId())
                .build();
    }
}
