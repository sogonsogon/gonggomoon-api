package com.sogonsogon.gonggomoon.domain.experience.domain;

import java.util.List;

public interface FileAssetRepository {

    FileAsset save(FileAsset fileAsset);

    List<FileAsset> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
