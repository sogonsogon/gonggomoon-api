package com.sogonsogon.gonggomoon.domain.file.api.response;

import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResult;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResultItem;
import lombok.Builder;

import java.util.List;

/**
 * 업로드된 파일 목록 반환 DTO
 */
@Builder
public record UploadedFileListResponse(
        int totalCount,
        List<UploadedFileListResultItem> contents
) {
    public static UploadedFileListResponse from (UploadedFileListResult result) {
        return UploadedFileListResponse.builder()
                .totalCount(result.totalCount())
                .contents(result.contents())
                .build();
    }
}
