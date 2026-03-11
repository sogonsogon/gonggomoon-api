package com.sogonsogon.gonggomoon.domain.strategy.generator;

import com.sogonsogon.gonggomoon.domain.strategy.generator.result.InterviewStrategyQuestionSet;

public interface InterviewStrategyQuestionSetGenerator {
    public InterviewStrategyQuestionSet generate(Long fileAssetId);
}
