package com.sogonsogon.gonggomoon.domain.post.infrastructure;

import com.sogonsogon.gonggomoon.domain.post.domain.Platform;
import com.sogonsogon.gonggomoon.domain.post.domain.PlatformRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformJpaRepositoryImpl extends JpaRepository<Platform, Long>, PlatformRepository {
}
