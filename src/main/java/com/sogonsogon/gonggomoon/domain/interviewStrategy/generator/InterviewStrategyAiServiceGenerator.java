package com.sogonsogon.gonggomoon.domain.interviewStrategy.generator;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewStrategyAiServiceGenerator implements InterviewStrategyQuestionSetGenerator{

    private final AiService aiService;

    @Override
    public void request(Long userId, Long interviewStrategyId) {
        aiService.requestInterviewStrategyGeneration(userId, interviewStrategyId);
    }
}
