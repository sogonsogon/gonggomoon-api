package com.sogonsogon.gonggomoon.domain.strategy.domain;

import java.util.List;
import java.util.Optional;

public interface InterviewStrategyRepository {
    Optional<InterviewStrategy> findByIdAndUserId(Long interviewStrategyId, Long userId);

    List<InterviewStrategy> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    InterviewStrategy save(InterviewStrategy interviewStrategy);
}