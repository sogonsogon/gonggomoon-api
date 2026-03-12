package com.sogonsogon.gonggomoon.domain.experience.domain;

import java.util.List;
import java.util.Optional;

public interface FileAssetRepository {
    Optional<FileAsset> findByIdAndUserId(Long id, Long userId);

    Optional<FileAsset> findById(Long id);

    FileAsset save(FileAsset fileAsset);

    void delete(FileAsset fileAsset);

    List<FileAsset> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<FileAsset> findAllByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, DocumentCategory category);
}
