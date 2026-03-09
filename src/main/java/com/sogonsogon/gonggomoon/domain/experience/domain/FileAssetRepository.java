package com.sogonsogon.gonggomoon.domain.experience.domain;

import java.util.List;
import java.util.Optional;

public interface FileAssetRepository {
    Optional<FileAsset> findByIdAndUserId(Long id, Long userId);

    FileAsset save(FileAsset fileAsset);

    void delete(FileAsset fileAsset);

    List<FileAsset> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
