package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import lombok.Builder;

import java.time.Instant;

/**
 * 업로드 파일 객체
 * @param fileAssetId
 * @param category
 * @param originalFileName
 * @param sizeBytes
 * @param createdAt
 */
@Builder
public record UploadedFileListResultItem(
        Long fileAssetId,
        DocumentCategory category,
        String originalFileName,
        Long sizeBytes,
        Instant createdAt
) {
    public static UploadedFileListResultItem from (FileAsset fileAsset) {
        return UploadedFileListResultItem.builder()
                .fileAssetId(fileAsset.getId())
                .category(fileAsset.getCategory())
                .originalFileName(fileAsset.getOriginalFileName())
                .sizeBytes(fileAsset.getSizeBytes())
                .createdAt(fileAsset.getCreatedAt())
                .build();
    }
}
