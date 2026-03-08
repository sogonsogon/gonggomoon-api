package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import lombok.Builder;

import java.util.List;

@Builder
public record UploadedFileListResult(
        int totalCount,
        List<UploadedFileListResultItem> contents
) {
    public static UploadedFileListResult from (List<FileAsset> fileAssets) {
        List<UploadedFileListResultItem> items = fileAssets.stream()
                .map(UploadedFileListResultItem::from)
                .toList();

        return UploadedFileListResult.builder()
                .totalCount(items.size())
                .contents(items)
                .build();
    }
}
