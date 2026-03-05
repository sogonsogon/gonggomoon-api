package com.sogonsogon.gonggomoon.domain.experience.infrastructure;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceJpaRepository
        extends JpaRepository<Experience, Long>, ExperienceRepository {

}
