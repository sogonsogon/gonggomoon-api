package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractionAiServerRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.InterviewStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.ai.error.AiErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.error.PostAnalysisErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.error.PostErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final InterviewStrategyRepository interviewStrategyRepository;
    private final AiServerClient aiServerClient;
    private final AiJobSseService aiJobSseService;
    private final PostRepository postRepository;
    private final PostAnalysisRepository postAnalysisRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /*
    * AI 서버에 경험 추출 요청을 처리하는 비즈니스 로직
    *
    * @param request 경험 추출 요청 DTO
    * @return 경험 추출 응답 DTO
    * */
    public ExperienceExtractResponse requestExperienceExtraction(Long userId, List<Long> fileAssetIds) {
        LocalDate generatedDate = Instant.now().atZone(KST).toLocalDate();

        // ExtractedExperience 엔티티 생성 (fileAssetId 1건당 추출 작업 1건)
        List<ExtractedExperience> extractedExperiences = fileAssetIds.stream()
            .map(fileAssetId -> ExtractedExperience.create(userId, fileAssetId, generatedDate))
            .toList();
        Iterable<ExtractedExperience> savedExtractedExperienceIterable = extractedExperienceRepository.saveAll(extractedExperiences);
        List<ExtractedExperience> savedExtractedExperiences = StreamSupport
            .stream(savedExtractedExperienceIterable.spliterator(), false)
            .toList();

        List<Long> savedExtractedExperienceIds = savedExtractedExperiences.stream()
            .map(ExtractedExperience::getId)
            .toList();

        List<UUID> savedExtractedExperiencePublicIds = savedExtractedExperiences.stream()
            .map(ExtractedExperience::getPublicId)
            .toList();

        // 워커가 S3에서 다운로드할 파일과 결과 매핑용 (file_asset_id ↔ extracted_experience_id) 쌍
        List<ExperienceExtractionAiServerRequest.FileAssetTarget> fileAssetTargets = savedExtractedExperiences.stream()
            .map(extractedExperience -> new ExperienceExtractionAiServerRequest.FileAssetTarget(
                extractedExperience.getFileAssetId(),
                extractedExperience.getId()
            ))
            .toList();

        // AI 워커에 경험 추출 요청 전송 (Cloud Tasks)
        // 파일 1건당 요청 1건이므로 첫 번째 작업 ID가 콜백 id로 에코백된다.
        try {
            aiServerClient.requestExperienceExtraction(savedExtractedExperienceIds.get(0), userId, fileAssetTargets);
        } catch (RuntimeException exception) {
            savedExtractedExperiences.forEach(extractedExperience ->
                extractedExperience.updateStatus(ExtractionStatus.FAILED));
            extractedExperienceRepository.saveAll(savedExtractedExperiences);
            throw exception;
        }

        return new ExperienceExtractResponse(savedExtractedExperiencePublicIds);
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
        Long postAnalysisId) {
        requestPortfolioStrategyGeneration(userId, portfolioStrategyId, experiences, null, "마스터", postAnalysisId);
    }

    public void requestPortfolioStrategyGeneration(
        Long userId,
        Long portfolioStrategyId,
        List<Experience> experiences,
        String positionType,
        String industryType,
        Long postAnalysisId) {

        // 워커는 DB 조회 없이 인라인 데이터만으로 처리하므로, 경험 목록과 공고 분석 결과를 모두 담아 보낸다.
        List<PortfolioStrategyRequest.ExperienceInput> experienceInputs = experiences.stream()
            .map(PortfolioStrategyRequest.ExperienceInput::from)
            .toList();

        PostAnalysis postAnalysis = postAnalysisRepository.findById(postAnalysisId)
            .orElseThrow(() -> new BaseException(PostAnalysisErrorCode.NOT_FOUND));
        PortfolioStrategyRequest.PostAnalysisInput postAnalysisInput =
            new PortfolioStrategyRequest.PostAnalysisInput(postAnalysis.getTitle(), postAnalysis.getSummary());

        // AI 서버에 포트폴리오 전략 생성 요청 전송
        try {
            aiServerClient.requestPortfolioStrategyGeneration(
                portfolioStrategyId,
                userId,
                experienceInputs,
                positionType,
                industryType,
                postAnalysisInput
            );
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

    public void requestPostAnalysis(Long userId, Long postId, Long fileAssetId) {
        try {
            aiServerClient.requestPostAnalysis(userId, postId, fileAssetId);
        } catch (RuntimeException exception) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(PostErrorCode.POST_NOT_FOUND));
            post.failed();
            postRepository.save(post);
            throw new BaseException(AiErrorCode.AI_SERVER_ERROR);
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
            case POST_ANALYSIS -> getPostAnalysisStatus(userId, request.id());
            default -> throw new BaseException(AiErrorCode.INVALID_TYPE);
        };

        AiFunctionStatus resolvedStatus = AiFunctionStatus.valueOf(status);

        UUID strategyId = null;
        if (request.type() == AiFunctions.POST_ANALYSIS && resolvedStatus == AiFunctionStatus.READY) {
            strategyId = getStrategyIdByPostId(userId, request.id());
        }

        // DTO 생성 및 반환
        return new AiFunctionStatusResponse(
            request.type(),
            request.id(),
            AiFunctionStatus.valueOf(status),
            strategyId,
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

    private boolean isTerminalStatus(AiFunctionStatus status) {
        return status == AiFunctionStatus.READY || status == AiFunctionStatus.FAILED;
    }

    private String getExperienceExtractionStatus(Long userId, UUID extractedExperienceId) {
        ExtractedExperience foundExtractedExperience = extractedExperienceRepository.findByPublicIdAndUserId(extractedExperienceId, userId)
            .orElseThrow(() -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND));

        return foundExtractedExperience.getStatus().name();
    }

    private String getPortfolioStrategyGenerationStatus(Long userId, UUID portfolioStrategyId) {
        return portfolioStrategyRepository.findByPublicIdAndUserId(portfolioStrategyId, userId)
            .map(strategy -> strategy.getStatus().name())
            .orElseThrow(() -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND));
    }

    private String getInterviewStrategyGenerationStatus(Long userId, UUID interviewStrategyId) {
        return interviewStrategyRepository.findByPublicIdAndUserId(interviewStrategyId, userId)
            .map(strategy -> strategy.getStatus().name())
            .orElseThrow(() -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND));
    }

    private String getPostAnalysisStatus(Long userId, UUID postId) {
        Post foundPost = postRepository.findByPublicIdAndCreatedBy(postId, userId)
            .orElseThrow(() -> new BaseException(PostErrorCode.POST_NOT_FOUND));

        return switch (foundPost.getStatus()) {
            case PENDING -> AiFunctionStatus.PROCESSING.name();
            case SUCCESS -> AiFunctionStatus.READY.name();
            case FAILED -> AiFunctionStatus.FAILED.name();
        };
    }

    private UUID getStrategyIdByPostId(Long userId, UUID postId) {
        Post post = postRepository.findByPublicIdAndCreatedBy(postId, userId)
                .orElseThrow(() -> new BaseException(PostErrorCode.POST_NOT_FOUND));

        return portfolioStrategyRepository.findByPostIdAndUserId(post.getId(), userId)
                .map(PortfolioStrategy::getPublicId)
                .orElse(null);
    }
}
