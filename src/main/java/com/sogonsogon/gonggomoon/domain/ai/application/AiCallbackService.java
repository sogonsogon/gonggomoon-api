package com.sogonsogon.gonggomoon.domain.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiJobStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.BaseCallbackRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.ExperienceResultMapper;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.InterviewQuestionResultMapper;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewGenerateStatus;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
@RequiredArgsConstructor
public class AiCallbackService {

    private final ExperienceResultMapper experienceResultMapper;
    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final InterviewStrategyRepository interviewStrategyRepository;
    private final InterviewQuestionResultMapper interviewQuestionResultMapper;
    private final AiJobSseService aiJobSseService;

    private final ObjectMapper objectMapper;

    @Transactional
    public void createExtractedExperience(BaseCallbackRequest request) {
        JsonNode resultsNode = request.result();

        if (resultsNode == null || !resultsNode.isArray()) {
            throw new BaseException(ExtractedExperienceErrorCode.INVALID_RESULT_FORMAT);
        }

        List<JsonNode> callbackItems = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        for (JsonNode itemNode : resultsNode) {
            long extractedExperienceId = itemNode.path("extracted_experience_id").asLong(0);

            if (extractedExperienceId == 0) {
                throw new BaseException(ExtractedExperienceErrorCode.INVALID_RESULT_FORMAT);
            }

            callbackItems.add(itemNode);
            ids.add(extractedExperienceId);
        }

        // 경험 추출 실패 업데이트
        if (request.status() == AiJobStatus.FAILED) {
            List<ExtractedExperience> experiencesToUpdate = extractedExperienceRepository.findAllById(ids);
            for (ExtractedExperience experience : experiencesToUpdate) {
                experience.updateStatus(ExtractionStatus.FAILED);
            }
            extractedExperienceRepository.saveAll(experiencesToUpdate);
            notifyJobStatusAfterCommit(request.userId(), AiFunctions.EXTRACT_EXPERIENCE, ids, AiFunctionStatus.FAILED);
            return;
        }

        List<ExtractedExperience> foundExperiences = extractedExperienceRepository.findAllById(ids);

        Map<Long, ExtractedExperience> experienceMap = foundExperiences.stream()
            .collect(Collectors.toMap(ExtractedExperience::getId, Function.identity()));

        List<ExtractedExperience> entitiesToSave = new ArrayList<>();

        for (JsonNode itemNode : callbackItems) {
            long extractedExperienceId = itemNode.path("extracted_experience_id").asLong();

            ExtractedExperience foundExperience = experienceMap.get(extractedExperienceId);
            if (foundExperience == null) {
                throw new BaseException(ExtractedExperienceErrorCode.NOT_FOUND);
            }

            Experiences experiences = experienceResultMapper.toExperiencesFromCallbackItem(itemNode);

            foundExperience.updateExperiences(experiences);
            foundExperience.updateStatus(ExtractionStatus.READY);

            entitiesToSave.add(foundExperience);
        }

        extractedExperienceRepository.saveAll(entitiesToSave);
        notifyJobStatusAfterCommit(request.userId(), AiFunctions.EXTRACT_EXPERIENCE, ids, AiFunctionStatus.READY);
    }

    @Transactional
    public void updatePortfolioStrategy(BaseCallbackRequest request) {

        // id 값으로 찾아오기
        PortfolioStrategy fountStrategy =portfolioStrategyRepository.findByIdAndUserId(request.id(), request.userId()).orElseThrow(
            () -> new BaseException(PortfolioStrategyErrorCode.NOT_FOUND)
        );
        // AI 작업 실패로 업데이트
        if (request.status() == AiJobStatus.FAILED) {
            fountStrategy.updateStatus(PortfolioStrategyGenerateStatus.FAILED);
            portfolioStrategyRepository.save(fountStrategy);
            notifyJobStatusAfterCommit(request.userId(), AiFunctions.PORTFOLIO_STRATEGY, request.id(), AiFunctionStatus.FAILED);
            return;
        }

        // 결과를 텍스트로 저장 (내부적으로 status 업데이트도 같이 이루어짐)
        JsonNode resultNode = request.result();
        if (resultNode == null) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_EMPTY);
        }

        JsonNode portfolioStrategyNode = resultNode.get("portfolioStrategy");
        if (portfolioStrategyNode == null || portfolioStrategyNode.isNull()) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_EMPTY);
        }

        System.out.println("AI 콜백으로 받은 포트폴리오 전략 결과: " + portfolioStrategyNode);

        String resultJson;
        try {
            resultJson = objectMapper.writeValueAsString(portfolioStrategyNode);
        } catch (JsonProcessingException e) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_SERIALIZATION_FAILED);
        }

        fountStrategy.addResult(resultJson);

        // 명시적으로 업데이트를 표현하기 위해 save() 호출 (영속성 컨텍스트에 의해 자동으로 업데이트가 될 수 있지만, 명시적으로 표현)
        portfolioStrategyRepository.save(fountStrategy);
        notifyJobStatusAfterCommit(request.userId(), AiFunctions.PORTFOLIO_STRATEGY, request.id(), AiFunctionStatus.READY);

    }

    @Transactional
    public void updateInterviewStrategy(BaseCallbackRequest request) {

        // id 값으로 찾아오기
        InterviewStrategy foundStrategy = interviewStrategyRepository.findByIdAndUserId(request.id(), request.userId())
            .orElseThrow(
                () -> new BaseException(InterviewStrategyErrorCode.NOT_FOUND)
            );

        // AI 작업 실패로 업데이트
        if (request.status() == AiJobStatus.FAILED) {
            foundStrategy.updateStateFailed();
            interviewStrategyRepository.save(foundStrategy);
            notifyJobStatusAfterCommit(request.userId(), AiFunctions.INTERVIEW_STRATEGY, request.id(), AiFunctionStatus.FAILED);
            return;
        }

        // 결과에서 questions 추출하기 (실제 필드명은 AI 서버에서 보내주는 결과에 따라 달라질 수 있음)
        JsonNode questionsNode = request.result().get("questions");

        // interview strategy에 questions 저장하기
        List<InterviewQuestion> interviewQuestions = interviewQuestionResultMapper.toInterviewQuestions(questionsNode, foundStrategy);
        foundStrategy.addQuestions(interviewQuestions);

        // interview strategy status를 READY로 업데이트 (완전히 생성이 완료된 상태)
        foundStrategy.updateStatusReady();

        // 명시적으로 업데이트를 표현하기 위해 save() 호출 (영속성 컨텍스트에 의해 자동으로 업데이트가 될 수 있지만, 명시적으로 표현)
        interviewStrategyRepository.save(foundStrategy);
        notifyJobStatusAfterCommit(request.userId(), AiFunctions.INTERVIEW_STRATEGY, request.id(), AiFunctionStatus.READY);
    }

    private void notifyJobStatusAfterCommit(Long userId, AiFunctions type, List<Long> ids, AiFunctionStatus status) {
        runAfterCommit(() -> ids.forEach(id -> notifyJobStatus(userId, type, id, status)));
    }

    private void notifyJobStatusAfterCommit(Long userId, AiFunctions type, Long id, AiFunctionStatus status) {
        runAfterCommit(() -> notifyJobStatus(userId, type, id, status));
    }

    private void notifyJobStatus(Long userId, AiFunctions type, Long id, AiFunctionStatus status) {
        aiJobSseService.send(userId, new AiFunctionStatusResponse(type, id, status, null));
        aiJobSseService.complete(userId, type, id);
    }

    private void runAfterCommit(Runnable runnable) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            runnable.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                runnable.run();
            }
        });
    }
}
