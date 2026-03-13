package com.sogonsogon.gonggomoon.domain.post.domain;

import java.util.List;
import java.util.Optional;

public interface PlatformRepository {

    Optional<Platform> findById(Long Id);

    List<Platform> findAll();
}
