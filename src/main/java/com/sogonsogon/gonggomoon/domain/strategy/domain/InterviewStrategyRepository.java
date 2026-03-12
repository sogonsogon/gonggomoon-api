package com.sogonsogon.gonggomoon.domain.strategy.domain;

import java.util.List;

public interface InterviewStrategyRepository {

    List<InterviewStrategy> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    InterviewStrategy save(InterviewStrategy interviewStrategy);
}