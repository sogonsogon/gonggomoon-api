package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.util.Optional;

public interface ExtractedExperienceRepository {

    ExtractedExperience save(ExtractedExperience extractedExperience);

    <S extends ExtractedExperience> Iterable<S> saveAll(Iterable<S> extractedExperiences);

    Optional<ExtractedExperience> findById(Long id);
}
