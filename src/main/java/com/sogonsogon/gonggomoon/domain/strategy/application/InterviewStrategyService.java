package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.domain.GenerateStatus;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.InterviewStrategyQuestionSetGenerator;
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

    @Value("${strategy.interview.daily-limit-enabled:true}")
    private boolean dailyLimitEnabled;

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
        LocalDate today = now.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();

        /**
         * 질문이 이미 있는지 검증
         */
        if (dailyLimitEnabled && interviewStrategyRepository.existsByUserIdAndGeneratedDate(userId, today)) {
            throw new BaseException(InterviewStrategyErrorCode.ALREADY_CREATED_TODAY);
        }

        FileAsset fileAsset = fileAssetRepository.findByIdAndUserId(req.fileAssetId(), userId)
                .orElseThrow(() -> new BaseException(InterviewStrategyErrorCode.FILE_ASSET_NOT_FOUND));

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

        if (interviewStrategy.getStatus() == GenerateStatus.PROCESSING) {
            throw new BaseException(InterviewStrategyErrorCode.RESULT_NOT_READY);
        }

        if (interviewStrategy.getStatus() == GenerateStatus.FAILED) {
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
