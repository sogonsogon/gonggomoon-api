package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.util.Optional;

public interface ExtractedExperienceRepository {

    ExtractedExperience save(ExtractedExperience extractedExperience);

    Optional<ExtractedExperience> findById(Long id);
}
