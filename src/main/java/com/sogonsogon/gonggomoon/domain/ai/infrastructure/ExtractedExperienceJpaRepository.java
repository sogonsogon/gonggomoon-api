package com.sogonsogon.gonggomoon.domain.ai.infrastructure;

import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractedExperienceJpaRepository extends JpaRepository<ExtractedExperience, Long>,
    ExtractedExperienceRepository {

}
