package com.sogonsogon.gonggomoon.domain.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiJobStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.BaseCallbackRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.error.PostAnalysisErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.ExperienceResultMapper;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.InterviewQuestionResultMapper;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.file.application.FileAssetService;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewGenerateStatus;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.PortfolioStrategyService;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;
import com.sogonsogon.gonggomoon.domain.post.error.PostErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ExperienceRepository experienceRepository;
    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final InterviewStrategyRepository interviewStrategyRepository;
    private final InterviewQuestionResultMapper interviewQuestionResultMapper;
    private final AiUsagePolicyService aiUsagePolicyService;
    private final AiJobSseService aiJobSseService;
    private final FileAssetService fileAssetService;
    private final PostRepository postRepository;
    private final PostAnalysisRepository postAnalysisRepository;
    private final PortfolioStrategyService portfolioStrategyService;

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
            boolean shouldRefund = experiencesToUpdate.stream()
                .anyMatch(experience -> experience.getStatus() == ExtractionStatus.PROCESSING);
            for (ExtractedExperience experience : experiencesToUpdate) {
                experience.updateStatus(ExtractionStatus.FAILED);
            }
            extractedExperienceRepository.saveAll(experiencesToUpdate);
            if (shouldRefund && !experiencesToUpdate.isEmpty()) {
                aiUsagePolicyService.refund(
                    experiencesToUpdate.get(0).getUserId(),
                    AiUsageType.EXPERIENCE_EXTRACTION,
                    experiencesToUpdate.get(0).getGeneratedDate()
                );
            }
            // 실패한 경우에도 더 이상 처리되지 않으므로 임시 파일을 정리한다.
            deleteTempFilesAfterCommit(collectFileAssetIds(experiencesToUpdate));
            notifyJobStatusAfterCommit(request.userId(), AiFunctions.EXTRACT_EXPERIENCE, ids, AiFunctionStatus.FAILED);
            return;
        }

        List<ExtractedExperience> foundExperiences = extractedExperienceRepository.findAllById(ids);

        Map<Long, ExtractedExperience> experienceMap = foundExperiences.stream()
            .collect(Collectors.toMap(ExtractedExperience::getId, Function.identity()));

        List<ExtractedExperience> entitiesToSave = new ArrayList<>();
        List<Experience> experiencesToSave = new ArrayList<>();

        for (JsonNode itemNode : callbackItems) {
            long extractedExperienceId = itemNode.path("extracted_experience_id").asLong();

            ExtractedExperience foundExperience = experienceMap.get(extractedExperienceId);
            if (foundExperience == null) {
                throw new BaseException(ExtractedExperienceErrorCode.NOT_FOUND);
            }

            if (foundExperience.getStatus() != ExtractionStatus.PROCESSING) {
                continue;
            }

            Experiences experiences = experienceResultMapper.toExperiencesFromCallbackItem(itemNode);

            foundExperience.updateStatus(ExtractionStatus.READY);

            entitiesToSave.add(foundExperience);
            experiencesToSave.addAll(toExperiences(foundExperience.getUserId(), experiences));
        }

        experienceRepository.saveAll(experiencesToSave);
        extractedExperienceRepository.saveAll(entitiesToSave);
        // 추출이 끝난 원본 파일은 더 이상 필요 없으므로 정리한다. (커밋 이후 실행)
        deleteTempFilesAfterCommit(collectFileAssetIds(foundExperiences));
        notifyJobStatusAfterCommit(request.userId(), AiFunctions.EXTRACT_EXPERIENCE, ids, AiFunctionStatus.READY);
    }

    /**
     * AI 워커로부터 "공고 URL 분석" 작업의 처리 결과를 콜백으로 전달받아 반영하는 메서드.
     *
     * 워커는 요청 하나(post_id 1건)당 콜백 1건을 보내는 것을 기본으로 하지만,
     * Cloud Tasks 특성상 동일한 콜백이 중복으로 도착할 수 있다(at-least-once 전송).
     * 이 메서드는 Post.status를 기준으로 이미 처리된 요청을 스킵하여 중복 처리를 방지한다.
     *
     * 처리 흐름:
     * 1) 콜백 바디(result)에서 post_id 목록을 파싱
     * 2) 실패 콜백이면 → 해당 Post를 FAILED로 변경하고 임시 파일 정리 후 종료
     * 3) 성공 콜백이면 → PostAnalysis(분석 결과) 저장 → Post를 SUCCESS로 연결
     *    → PortfolioStrategy DRAFT row 생성까지 이어서 처리
     */
    @Transactional
    public void createPostAnalysis(BaseCallbackRequest request) {

        // ────────────────────────────────────────────────────────
        // 1. 콜백 바디 파싱 및 기본 검증
        //    result는 [{ "post_id": 1, "title": "...", "summary": "..." }, ...] 형태의 배열이어야 한다.
        // ────────────────────────────────────────────────────────
        JsonNode resultsNode = request.result();

        if (resultsNode == null || !resultsNode.isArray()) {
            throw new BaseException(PostAnalysisErrorCode.INVALID_CALLBACK_FORMAT);
        }

        List<JsonNode> callbackItems = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        for (JsonNode itemNode : resultsNode) {
            long postId = itemNode.path("post_id").asLong(0);
            if (postId == 0) {
                throw new BaseException(PostAnalysisErrorCode.INVALID_CALLBACK_FORMAT);
            }
            callbackItems.add(itemNode);
            ids.add(postId);
        }

        // 콜백에 포함된 post_id들에 해당하는 Post를 한 번에 조회 (N+1 방지)
        List<Post> foundPosts = postRepository.findAllById(ids);
        Map<Long, Post> postMap = foundPosts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        // ────────────────────────────────────────────────────────
        // 2. 실패 케이스
        //    분석 자체가 실패했으므로 PostAnalysis/PortfolioStrategy는 생성하지 않는다.
        //    Post 상태만 FAILED로 남기고, 더 이상 쓰이지 않는 임시 파일을 정리한다.
        // ────────────────────────────────────────────────────────
        if (request.status() == AiJobStatus.FAILED) {

            // 주간 이용 횟수 환불 대상 여부를 미리 판단해둔다.
            // (아직 PENDING인 요청만 환불 대상 - 이미 실패 처리된 요청을 중복 환불하지 않기 위함)
            // 지금은 사용하지 않음
            boolean shouldRefund = foundPosts.stream()
                    .anyMatch(post -> post.getStatus() == PostStatus.PENDING);

            for (Post post : foundPosts) {
                post.failed();
            }
            postRepository.saveAll(foundPosts);

            // TODO: 주간 이용 횟수 제한 기능이 아직 비활성화 상태라 주석 처리됨.
            //  기능 복원 시 아래 refund 호출을 활성화할 것.
            //  단, 요청 시점과 콜백 시점의 날짜(주)가 다를 수 있으므로
            //  Post 생성 시점에 저장해둔 generatedDate를 기준으로 계산해야 한다.
//        if (shouldRefund && !foundPosts.isEmpty()) {
//            aiUsagePolicyService.refund(foundPosts.get(0).getCreatedBy(), AiUsageType.POST_ANALYSIS);
//        }

            cleanupTemporaryFiles(foundPosts);
            notifyJobStatusAfterCommit(request.userId(), AiFunctions.POST_ANALYSIS, ids, AiFunctionStatus.FAILED);
            return;
        }

        // ────────────────────────────────────────────────────────
        // 3. 성공 케이스 - PostAnalysis 생성
        //    이미 SUCCESS/FAILED로 처리된 Post는 중복 콜백이므로 건너뛴다.
        //    (PostAnalysis.url은 unique 제약이라, 중복 저장을 시도하면 예외가 발생한다)
        // ────────────────────────────────────────────────────────
        List<PostAnalysis> analysesToSave = new ArrayList<>();

        for (JsonNode itemNode : callbackItems) {
            long postId = itemNode.path("post_id").asLong();
            Post foundPost = postMap.get(postId);

            if (foundPost == null) {
                throw new BaseException(PostErrorCode.POST_NOT_FOUND);
            }
            if (foundPost.getStatus() != PostStatus.PENDING) {
                continue; // 이미 처리된 요청(중복 콜백) - 스킵
            }

            String title = itemNode.path("title").asText(null);
            String summary = itemNode.path("summary").asText(null);

            analysesToSave.add(PostAnalysis.create(foundPost.getUrl(), title, summary));
        }

        List<PostAnalysis> savedAnalyses = postAnalysisRepository.saveAll(analysesToSave);

        // 방금 저장한 PostAnalysis를 url 기준으로 매핑해, 아래에서 Post와 다시 연결할 때 사용한다.
        // (postId를 직접 들고 있지 않으므로 url을 매개로 연결 - 동일 배치 내 url 중복 시 예외 위험 있음,
        //  현재는 요청이 1건씩만 발행되므로 문제되지 않음)
        Map<String, Long> urlToAnalysisId = savedAnalyses.stream()
                .collect(Collectors.toMap(PostAnalysis::getUrl, PostAnalysis::getId));

        // ────────────────────────────────────────────────────────
        // 4. 성공 케이스 - Post 연결 + 포트폴리오 전략 DRAFT 생성
        //    분석이 확정된 시점에, 유저가 이 분석 결과로 전략을 생성할 수 있도록
        //    PortfolioStrategy를 DRAFT 상태로 미리 만들어둔다.
        //    (같은 URL이어도 유저가 다른 소스로 전략을 여러 번 만들 수 있으므로 매번 새로 생성)
        // ────────────────────────────────────────────────────────
        List<Post> postsToSave = new ArrayList<>();
        for (JsonNode itemNode : callbackItems) {
            long postId = itemNode.path("post_id").asLong();
            Post foundPost = postMap.get(postId);

            if (foundPost == null || foundPost.getStatus() != PostStatus.PENDING) {
                continue;
            }

            Long analysisId = urlToAnalysisId.get(foundPost.getUrl());
            foundPost.success(analysisId);
            postsToSave.add(foundPost);

            portfolioStrategyService.createDraft(foundPost.getCreatedBy(), analysisId);
        }

        postRepository.saveAll(postsToSave);
        cleanupTemporaryFiles(foundPosts);
        notifyJobStatusAfterCommit(request.userId(), AiFunctions.POST_ANALYSIS, ids, AiFunctionStatus.READY);
    }

    /**
     * 분석이 종료된(성공/실패 무관) Post들이 갖고 있던 임시 rawContent 파일(S3)을 정리한다.
     * 더 이상 워커가 접근할 필요가 없어진 파일이므로, 콜백 처리 직후 삭제 대상이 된다.
     */
    private void cleanupTemporaryFiles(List<Post> posts) {
        List<Long> fileAssetIds = posts.stream()
                .map(Post::getFileAssetId)
                .filter(Objects::nonNull)
                .toList();
        fileAssetService.deleteTemporaryFiles(fileAssetIds);
    }

    private List<Long> collectFileAssetIds(List<ExtractedExperience> extractedExperiences) {
        return extractedExperiences.stream()
            .map(ExtractedExperience::getFileAssetId)
            .toList();
    }

    private void deleteTempFilesAfterCommit(List<Long> fileAssetIds) {
        if (fileAssetIds.isEmpty()) {
            return;
        }
        runAfterCommit(() -> fileAssetService.deleteTemporaryFiles(fileAssetIds));
    }

    private List<Experience> toExperiences(Long userId, Experiences experiences) {
        return experiences.getExperiences().stream()
            .map(item -> Experience.create(
                userId,
                item.getTitle(),
                item.getExperienceType(),
                item.getExperienceContent(),
                toStartDate(item.getStartDate()),
                toEndDate(item.getEndDate())
            ))
            .toList();
    }

    private LocalDate toStartDate(YearMonth yearMonth) {
        return yearMonth == null ? null : yearMonth.atDay(1);
    }

    private LocalDate toEndDate(YearMonth yearMonth) {
        return yearMonth == null ? null : yearMonth.atEndOfMonth();
    }

    @Transactional
    public void updatePortfolioStrategy(BaseCallbackRequest request) {

        // id 값으로 찾아오기
        PortfolioStrategy fountStrategy =portfolioStrategyRepository.findByIdAndUserId(request.id(), request.userId()).orElseThrow(
            () -> new BaseException(PortfolioStrategyErrorCode.NOT_FOUND)
        );
        // AI 작업 실패로 업데이트
        if (request.status() == AiJobStatus.FAILED) {
            boolean shouldRefund = fountStrategy.getStatus() == PortfolioStrategyGenerateStatus.PROCESSING;
            fountStrategy.updateStatus(PortfolioStrategyGenerateStatus.FAILED);
            portfolioStrategyRepository.save(fountStrategy);
            if (shouldRefund) {
                aiUsagePolicyService.refund(
                    fountStrategy.getUserId(),
                    AiUsageType.PORTFOLIO_STRATEGY,
                    fountStrategy.getGeneratedDate()
                );
            }
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
            boolean shouldRefund = foundStrategy.getStatus() == InterviewGenerateStatus.PROCESSING;
            foundStrategy.updateStateFailed();
            interviewStrategyRepository.save(foundStrategy);
            if (shouldRefund) {
                aiUsagePolicyService.refund(
                    foundStrategy.getUserId(),
                    AiUsageType.INTERVIEW_STRATEGY,
                    foundStrategy.getGeneratedDate()
                );
            }
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
