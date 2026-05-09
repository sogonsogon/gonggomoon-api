package com.sogonsogon.gonggomoon.domain.interviewStrategy.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewGenerateStatus;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.generator.InterviewStrategyQuestionSetGenerator;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewStrategyService {

    private final FileAssetRepository fileAssetRepository;
    private final InterviewStrategyRepository interviewStrategyRepository;
    private final InterviewStrategyQuestionSetGenerator interviewStrategyQuestionSetGenerator;
    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${strategy.interview.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 면접 전략 질문 생성 서비스
     */
    @Transactional
    public GenerateInterviewQuestionSetResult generate(Long userId, GenerateInterviewQuestionSetRequest req) {

        if (req.fileAssetId() == null) {
            throw new BaseException(InterviewStrategyErrorCode.FILE_ASSET_ID_REQUIRED);
        }

        /**
         * now : 기준 시각 1개
         * today : 그 시각에서 파생된 날짜
         */
        Instant now = Instant.now();
        LocalDate today = now.atZone(KST).toLocalDate();

        FileAsset fileAsset = fileAssetRepository.findByIdAndUserId(req.fileAssetId(), userId)
                .orElseThrow(() -> new BaseException(InterviewStrategyErrorCode.FILE_ASSET_NOT_FOUND));

        /**
         * 이번 주 성공한 질문 생성 횟수를 검증
         */
        if (weeklyLimitEnabled && !aiUsagePolicyService.reserve(userId, AiUsageType.INTERVIEW_STRATEGY)) {
            throw new BaseException(InterviewStrategyErrorCode.WEEKLY_LIMIT_EXCEEDED);
        }

        // 면접 질문 생성
        InterviewStrategy interviewStrategy = InterviewStrategy.create(userId, req.fileAssetId(), now, today);

        InterviewStrategy savedInterviewStrategy = interviewStrategyRepository.save(interviewStrategy);

        interviewStrategyQuestionSetGenerator.request(userId, interviewStrategy.getId());

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

        if (interviewStrategy.getStatus() == InterviewGenerateStatus.PROCESSING) {
            throw new BaseException(InterviewStrategyErrorCode.RESULT_NOT_READY);
        }

        if (interviewStrategy.getStatus() == InterviewGenerateStatus.FAILED) {
            throw new BaseException(InterviewStrategyErrorCode.GENERATION_FAILED);
        }

        List<InterviewQuestion> questions = interviewStrategy.getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new BaseException(InterviewStrategyErrorCode.QUESTION_EMPTY);
        }

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
