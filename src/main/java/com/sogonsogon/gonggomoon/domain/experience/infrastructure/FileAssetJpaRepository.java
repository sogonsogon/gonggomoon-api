package com.sogonsogon.gonggomoon.domain.experience.infrastructure;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAssetJpaRepository
        extends JpaRepository<FileAsset, Long>, FileAssetRepository {
}
