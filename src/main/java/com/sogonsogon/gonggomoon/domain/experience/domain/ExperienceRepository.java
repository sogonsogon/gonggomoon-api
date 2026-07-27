package com.sogonsogon.gonggomoon.domain.experience.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExperienceRepository {
    Optional<Experience> findByIdAndUserId(Long id, Long userId);

    Optional<Experience> findByPublicIdAndUserId(UUID publicId, Long userId);

    Experience save(Experience experience);

    <S extends Experience> Iterable<S> saveAll(Iterable<S> experiences);

    void delete(Experience experience);

    List<Experience> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Experience> findAllByIdInAndUserId(List<Long> ids, Long userId);

    List<Experience> findAllByPublicIdInAndUserId(List<UUID> publicIds, Long userId);
}
