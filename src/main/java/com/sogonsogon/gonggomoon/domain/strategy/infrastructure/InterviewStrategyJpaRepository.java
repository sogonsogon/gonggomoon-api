package com.sogonsogon.gonggomoon.domain.strategy.infrastructure;

import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewStrategyJpaRepository
        extends JpaRepository<InterviewStrategy, Long>, InterviewStrategyRepository {
}
