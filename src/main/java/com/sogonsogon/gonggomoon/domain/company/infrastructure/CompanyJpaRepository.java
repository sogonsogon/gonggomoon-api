package com.sogonsogon.gonggomoon.domain.company.infrastructure;

import com.sogonsogon.gonggomoon.domain.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
}
