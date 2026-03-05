package com.sogonsogon.gonggomoon.domain.experience.domain;

import java.util.Optional;

public interface ExperienceRepository {
    Optional<Experience> findById(Long id);

    Experience save(Experience experience);

    void delete(Experience experience);
}
