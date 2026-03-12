package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.InterviewStrategyQuestionSetGenerator;
import com.sogonsogon.gonggomoon.domain.strategy.generator.result.InterviewStrategyQuestionSet;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewStrategyService {

    private final FileAssetRepository fileAssetRepository;
    private final InterviewStrategyRepository interviewStrategyRepository;
    private final InterviewStrategyQuestionSetGenerator interviewStrategyQuestionSetGenerator;

    /**
     * 면접 전략 질문 생성 서비스
     */
    public GenerateInterviewQuestionSetResult generate(Long userId, GenerateInterviewQuestionSetRequest req) {

        if (req.fileAssetId() == null) {
            throw new BaseException(InterviewStrategyErrorCode.FILE_ASSET_ID_REQUIRED);
        }

        FileAsset fileAsset = fileAssetRepository.findByIdAndUserId(req.fileAssetId(), userId)
                .orElseThrow(() -> new BaseException(InterviewStrategyErrorCode.FILE_ASSET_NOT_FOUND));

        // 면접 질문 생성
        InterviewStrategyQuestionSet questionSet = interviewStrategyQuestionSetGenerator.generate(fileAsset.getId());

        List<InterviewQuestion> questions = questionSet.questions().stream()
                .map(item -> InterviewQuestion.create(
                        item.question(),
                        item.questionLevel()
                ))
                .toList();

        InterviewStrategy interviewStrategy = InterviewStrategy.create(userId, req.fileAssetId());
        interviewStrategy.addQuestions(questions);

        InterviewStrategy savedInterviewStrategy = interviewStrategyRepository.save(interviewStrategy);

        return GenerateInterviewQuestionSetResult.from(savedInterviewStrategy);
    }

    /**
     * 면접 전략 질문 목록 조회 서비스
     */
    public InterviewQuestionSetListResult getInterviewStrategiesList(Long userId) {
        List<InterviewStrategy> interviewStrategies = interviewStrategyRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return InterviewQuestionSetListResult.from(interviewStrategies);
    }

    /**
     * 면접 전략 질문 상세 조회 서비스
     */
    public InterviewStrategyDetailResult getInterviewStrategyDetail(Long interviewStrategyId, Long userId) {
        InterviewStrategy interviewStrategy = interviewStrategyRepository.findByIdAndUserId(interviewStrategyId, userId)
                .orElseThrow(() -> new BaseException(InterviewStrategyErrorCode.NOT_FOUND));

        FileAsset fileAsset = fileAssetRepository.findById(interviewStrategy.getFileAssetId())
                .orElseThrow(() -> new BaseException(InterviewStrategyErrorCode.FILE_ASSET_NOT_FOUND));

        return InterviewStrategyDetailResult.of(interviewStrategy, fileAsset);
    }

    /**
     * 면접 전략 질문 삭제 서비스
     */
    public void deleteInterviewStrategy(Long interviewStrategyId, Long userId) {
        InterviewStrategy interviewStrategy = interviewStrategyRepository.findByIdAndUserId(interviewStrategyId, userId)
                .orElseThrow(() -> new BaseException(InterviewStrategyErrorCode.NOT_FOUND));
        interviewStrategyRepository.delete(interviewStrategy);
    }
}
