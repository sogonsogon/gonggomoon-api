package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.util.Optional;
import java.util.List;

public interface ExtractedExperienceRepository {

    ExtractedExperience save(ExtractedExperience extractedExperience);

    <S extends ExtractedExperience> Iterable<S> saveAll(Iterable<S> extractedExperiences);

    Optional<ExtractedExperience> findById(Long id);

    Optional<ExtractedExperience> findByUserIdAndId(Long userId, Long id);

    List<ExtractedExperience> findAllById(Iterable<Long> ids);
}
