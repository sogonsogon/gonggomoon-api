package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ExtractedExperienceRepository {

    ExtractedExperience save(ExtractedExperience extractedExperience);

    <S extends ExtractedExperience> Iterable<S> saveAll(Iterable<S> extractedExperiences);

    Optional<ExtractedExperience> findById(Long id);

    Optional<ExtractedExperience> findByUserIdAndId(Long userId, Long id);

    Optional<ExtractedExperience> findByPublicIdAndUserId(UUID publicId, Long userId);

    List<ExtractedExperience> findAllById(Iterable<Long> ids);

    List<ExtractedExperience> findAllByStatusAndCreatedAtBefore(ExtractionStatus status, Instant createdAt);
}
