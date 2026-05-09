package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractionAiServerRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.InterviewStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.ai.error.AiErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final InterviewStrategyRepository interviewStrategyRepository;
    private final AiServerClient aiServerClient;
    private final AiJobSseService aiJobSseService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /*
    * AI 서버에 경험 추출 요청을 처리하는 비즈니스 로직
    *
    * @param request 경험 추출 요청 DTO
    * @return 경험 추출 응답 DTO
    * */
    public ExperienceExtractResponse requestExperienceExtraction(Long userId, List<Long> fileAssetIds) {

         // DTO 생성
        ExperienceExtractRequest request = new ExperienceExtractRequest(userId, fileAssetIds);
        LocalDate generatedDate = Instant.now().atZone(KST).toLocalDate();

        // ExtractedExperience 엔티티 생성

        List<ExtractedExperience> extractedExperiences = request.fileAssetIds().stream()
            .map(fileAssetId -> ExtractedExperience.create(request.userId(), fileAssetId, generatedDate))
            .toList();
        Iterable<ExtractedExperience> savedExtractedExperienceIterable = extractedExperienceRepository.saveAll(extractedExperiences);
        List<ExtractedExperience> savedExtractedExperiences = StreamSupport
            .stream(savedExtractedExperienceIterable.spliterator(), false)
            .toList();

        List<Long> savedExtractedExperienceIds = savedExtractedExperiences.stream()
            .map(ExtractedExperience::getId)
            .toList();

        // AI 서버에 경험 추출 요청 전송
        ExperienceExtractionAiServerRequest aiServerRequest = new ExperienceExtractionAiServerRequest(savedExtractedExperienceIds);
        try {
            aiServerClient.requestExperienceExtraction(aiServerRequest);
        } catch (RuntimeException exception) {
            savedExtractedExperiences.forEach(extractedExperience -> {
                extractedExperience.updateStatus(ExtractionStatus.FAILED);
            });
            extractedExperienceRepository.saveAll(savedExtractedExperiences);
            throw exception;
        }

        return new ExperienceExtractResponse(savedExtractedExperienceIds);
    }

    /*
    * AI 서버에 포트폴리오 전략 생성 요청을 처리하는 비즈니스 로직
    *
    * @param request 포트폴리오 전략 생성 요청 DTO
    * @return void (전략 생성 결과는 AI 서버에서 비동기로 처리될 예정)
    * */
    public void requestPortfolioStrategyGeneration(
        Long userId,
        Long portfolioStrategyId,
        List<Experience> experiences,
        String positionType,
        String industryType) {

        // DTO 생성
        PortfolioStrategyRequest request = new PortfolioStrategyRequest(
            userId,
            portfolioStrategyId,
            experiences,
            positionType,
            industryType
        );

        // AI 서버에 포트폴리오 전략 생성 요청 전송
        try {
            aiServerClient.requestPortfolioStrategyGeneration(request);
        } catch (RuntimeException exception) {
            portfolioStrategyRepository.findByIdAndUserId(portfolioStrategyId, userId)
                .ifPresent(portfolioStrategy -> {
                    portfolioStrategy.updateStatus(PortfolioStrategyGenerateStatus.FAILED);
                    portfolioStrategyRepository.save(portfolioStrategy);
                });
            throw exception;
        }
    }

    /*
     * AI 서버에 면접 전략 생성 요청을 처리하는 비즈니스 로직
     *
     * @param fileAssetId 면접 전략 생성에 필요한 파일 자산 ID
     * @return void (전략 생성 결과는 AI 서버에서 비동기로 처리될 예정)
     * */
    public void requestInterviewStrategyGeneration(Long userId, Long interviewStrategyId) {

        // DTO 생성
        InterviewStrategyRequest request = new InterviewStrategyRequest(userId, interviewStrategyId);

        // AI 서버에 포트폴리오 전략 생성 요청 전송
        try {
            aiServerClient.requestInterviewStrategyGeneration(request);
        } catch (RuntimeException exception) {
            interviewStrategyRepository.findByIdAndUserId(interviewStrategyId, userId)
                .ifPresent(interviewStrategy -> {
                    interviewStrategy.updateStateFailed();
                    interviewStrategyRepository.save(interviewStrategy);
                });
            throw exception;
        }
    }

    /*
    * AI 기능들에 대해서 상태값을 조회합니다.
    * */
    public AiFunctionStatusResponse checkAiFunctionStatus(Long userId, AiFunctionStatusRequest request) {

        // type에 따른 분기 처리
        if (request.type() == null) {
            throw new BaseException(AiErrorCode.INVALID_TYPE);
        }

        String status = switch (request.type()) {
            case EXTRACT_EXPERIENCE -> getExperienceExtractionStatus(userId, request.id());
            case PORTFOLIO_STRATEGY -> getPortfolioStrategyGenerationStatus(userId, request.id());
            case INTERVIEW_STRATEGY -> getInterviewStrategyGenerationStatus(userId, request.id());
            default -> throw new BaseException(AiErrorCode.INVALID_TYPE);
        };
        
        // DTO 생성 및 반환
        return new AiFunctionStatusResponse(
            request.type(),
            request.id(),
            AiFunctionStatus.valueOf(status),
            null
        );
    }

    public SseEmitter subscribe(Long userId, AiFunctionStatusRequest request) {
        SseEmitter emitter = aiJobSseService.register(userId, request.type(), request.id());

        try {
            AiFunctionStatusResponse response = checkAiFunctionStatus(userId, request);

            aiJobSseService.send(userId, response);

            if (isTerminalStatus(response.status())) {
                aiJobSseService.complete(userId, request.type(), request.id());
            }
        } catch (RuntimeException e) {
            aiJobSseService.complete(userId, request.type(), request.id());
            throw e;
        }

        return emitter;
    }

    public boolean isTerminalJobStatus(Long userId, AiFunctionStatusRequest request) {
        AiFunctionStatusResponse response = checkAiFunctionStatus(userId, request);
        return isTerminalStatus(response.status());
    }

    private boolean isTerminalStatus(AiFunctionStatus status) {
        return status == AiFunctionStatus.READY || status == AiFunctionStatus.FAILED;
    }

    private String getExperienceExtractionStatus(Long userId, Long extractedExperienceId) {
        ExtractedExperience foundExtractedExperience = extractedExperienceRepository.findByUserIdAndId(userId, extractedExperienceId)
            .orElseThrow(() -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND));

        return foundExtractedExperience.getStatus().name();
    }

    private String getPortfolioStrategyGenerationStatus(Long userId, Long extractedExperienceId) {
        return portfolioStrategyRepository.findByIdAndUserId(extractedExperienceId, userId)
            .map(strategy -> strategy.getStatus().name())
            .orElseThrow(() -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND));
    }

    private String getInterviewStrategyGenerationStatus(Long userId, Long extractedExperienceId) {
        return interviewStrategyRepository.findByIdAndUserId(extractedExperienceId, userId)
            .map(strategy -> strategy.getStatus().name())
            .orElseThrow(() -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND));
    }
}
