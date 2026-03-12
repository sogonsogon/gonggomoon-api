package com.sogonsogon.gonggomoon.domain.industry.domain;

import java.util.Optional;

public interface IndustryReportRepository {

    Optional<IndustryReport> findById(Long id);
}
