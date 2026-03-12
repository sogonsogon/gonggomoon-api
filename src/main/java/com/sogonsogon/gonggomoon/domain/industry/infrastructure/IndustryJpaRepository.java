package com.sogonsogon.gonggomoon.domain.industry.infrastructure;

import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndustryJpaRepository extends JpaRepository<Industry, Long>, IndustryRepository {

    Optional<Industry> findById(Long id);
}
