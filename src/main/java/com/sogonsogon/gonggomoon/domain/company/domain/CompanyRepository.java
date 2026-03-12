package com.sogonsogon.gonggomoon.domain.company.domain;

import java.util.Optional;

public interface CompanyRepository {

    Optional<Company> findById(Long id);
}
