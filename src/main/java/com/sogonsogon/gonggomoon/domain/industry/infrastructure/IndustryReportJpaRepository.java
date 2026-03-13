package com.sogonsogon.gonggomoon.domain.industry.infrastructure;

import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryReport;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryReportRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryReportJpaRepository extends JpaRepository<IndustryReport, Long>, IndustryReportRepository {
}
