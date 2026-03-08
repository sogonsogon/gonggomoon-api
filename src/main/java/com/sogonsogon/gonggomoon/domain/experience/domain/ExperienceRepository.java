package com.sogonsogon.gonggomoon.domain.experience.domain;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {
    Optional<Experience> findByIdAndUserId(Long id, Long userId);

    Experience save(Experience experience);

    void delete(Experience experience);

    List<Experience> findAllByUserIdOrderByUpdatedAtDesc(Long userId);
}