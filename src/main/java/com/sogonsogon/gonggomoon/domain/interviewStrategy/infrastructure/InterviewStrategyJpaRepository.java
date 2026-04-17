package com.sogonsogon.gonggomoon.domain.interviewStrategy.infrastructure;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewStrategyJpaRepository
        extends JpaRepository<InterviewStrategy, Long>, InterviewStrategyRepository {
}
