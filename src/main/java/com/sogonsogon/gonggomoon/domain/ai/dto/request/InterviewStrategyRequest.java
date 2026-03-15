package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InterviewStrategyRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("interview_strategy_id")
    Long interviewStrategyId
) {
}
