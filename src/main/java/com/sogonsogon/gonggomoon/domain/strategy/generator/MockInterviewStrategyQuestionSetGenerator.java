package com.sogonsogon.gonggomoon.domain.strategy.generator;

import com.sogonsogon.gonggomoon.domain.strategy.domain.QuestionLevel;
import com.sogonsogon.gonggomoon.domain.strategy.generator.result.InterviewQuestionItem;
import com.sogonsogon.gonggomoon.domain.strategy.generator.result.InterviewStrategyQuestionSet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockInterviewStrategyQuestionSetGenerator implements InterviewStrategyQuestionSetGenerator{

    @Override
    public InterviewStrategyQuestionSet generate(Long fileAssetId) {
        return InterviewStrategyQuestionSet.of(
                List.of(
                        new InterviewQuestionItem(
                                "본인이 팀 프로젝트에서 갈등을 해결했던 경험을 구체적으로 말씀해 주세요.",
                                QuestionLevel.HIGH
                        ),
                        new InterviewQuestionItem(
                                "프로젝트에서 기술 스택을 선택할 때 어떤 기준으로 의사결정을 하셨나요?",
                                QuestionLevel.MIDDLE
                        ),
                        new InterviewQuestionItem(
                                "예상치 못한 버그나 장애를 해결했던 경험을 설명해 주세요.",
                                QuestionLevel.HIGH
                        ),
                        new InterviewQuestionItem(
                                "팀원과 협업할 때 본인의 역할과 기여 방식은 무엇인가요?",
                                QuestionLevel.MIDDLE
                        ),
                        new InterviewQuestionItem(
                                "이 프로젝트에서 가장 어려웠던 기술적 문제는 무엇이었고 어떻게 해결했나요?",
                                QuestionLevel.HIGH
                        )
                )
        );
    }
}
