package com.sogonsogon.gonggomoon.domain.post.domain;

import java.util.Optional;

public interface PlatformRepository {

    Optional<Platform> findById(Long Id);
}
