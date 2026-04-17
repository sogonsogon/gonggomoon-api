package com.sogonsogon.gonggomoon.domain.interviewStrategy.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterviewStrategyRepository {
    Optional<InterviewStrategy> findByIdAndUserId(Long interviewStrategyId, Long userId);

    List<InterviewStrategy> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    InterviewStrategy save(InterviewStrategy interviewStrategy);

    void delete(InterviewStrategy interviewStrategy);

    boolean existsByUserIdAndGeneratedDate(Long userId, LocalDate today);

    int countByUserIdAndGeneratedDate(Long userId, LocalDate generatedDate);
}