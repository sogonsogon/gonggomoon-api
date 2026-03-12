package com.sogonsogon.gonggomoon.domain.industry.domain;

import java.util.Optional;

public interface IndustryRepository {

    Optional<Industry> findById(Long id);
}
