package com.sogonsogon.gonggomoon.domain.ai.infrastructure;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.HttpRequest;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.protobuf.ByteString;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractionAiServerRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.InterviewStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PostAnalysisAiServerRequest;
import com.sogonsogon.gonggomoon.domain.ai.error.AiErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiServerClient {

    // 신규 AI 워커의 단일 진입점. 워커는 body의 job_type으로 작업을 분기한다.
    private static final String TASKS_EXECUTE_PATH = "/tasks/execute";
    private static final String EXPERIENCE_EXTRACTION_JOB_TYPE = "EXPERIENCE_EXTRACTION";
    private static final String POST_ANALYSIS_JOB_TYPE = "POST_ANALYSIS";
    private static final String EXPERIENCE_EXTRACTION_CALLBACK_PATH = "/api/v1/callbacks/experience-extraction";
    private static final String POST_ANALYSIS_CALLBACK_PATH = "/api/v1/callbacks/post-analysis";

    private final CloudTasksClient cloudTasksClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.server.base-url}")
    private String aiServerBaseUrl;

    @Value("${ai.server.internal-api-key}")
    private String internalApiKey;

    @Value("${ai.callback.base-url}")
    private String callbackBaseUrl;

    @Value("${gcp.cloud-tasks.project-id}")
    private String projectId;

    @Value("${gcp.cloud-tasks.location}")
    private String location;

    @Value("${gcp.cloud-tasks.queue}")
    private String queue;

    /*
    * 경험 추출 요청을 Cloud Tasks 큐에 등록하는 메서드
    * 워커(/tasks/execute)가 기대하는 형식(job_type, user_id, callback_url, file_asset_ids)으로 변환한다.
    * 큐에 등록되면 Cloud Tasks가 워커로 HTTP POST를 비동기 전송한다.
    * */
    public void requestExperienceExtraction(Long extractionId, Long userId, List<ExperienceExtractionAiServerRequest.FileAssetTarget> fileAssetTargets) {
        ExperienceExtractionAiServerRequest request = new ExperienceExtractionAiServerRequest(
            extractionId,
            EXPERIENCE_EXTRACTION_JOB_TYPE,
            userId,
            callbackBaseUrl + EXPERIENCE_EXTRACTION_CALLBACK_PATH,
            fileAssetTargets
        );
        enqueue(TASKS_EXECUTE_PATH, request);
    }

    /*
     * 포트폴리오 요청을 Cloud Tasks 큐에 등록하는 메서드
     * */
    public void requestPortfolioStrategyGeneration(PortfolioStrategyRequest request) {
        enqueue(TASKS_EXECUTE_PATH, request);
    }

    /*
     * 면접 전략 생성 요청을 Cloud Tasks 큐에 등록하는 메서드
     * */
    public void requestInterviewStrategyGeneration(InterviewStrategyRequest request) {
        enqueue(TASKS_EXECUTE_PATH, request);
    }

    /*
     * 공고 분석 요청을 Cloud Tasks 큐에 등록하는 메서드
     * 워커는 S3에 업로드된 공고 원문(file_asset_id)을 내려받아 분석한다.
     * */
    public void requestPostAnalysis(Long userId, Long postId, Long fileAssetId) {
        PostAnalysisAiServerRequest request = new PostAnalysisAiServerRequest(
                postId,
                POST_ANALYSIS_JOB_TYPE,
                userId,
                callbackBaseUrl + POST_ANALYSIS_CALLBACK_PATH,
                fileAssetId
        );
        enqueue(TASKS_EXECUTE_PATH, request);
    }

    /*
     * 요청을 Cloud Tasks 태스크로 변환하여 큐에 등록한다.
     * 태스크는 AI 서버의 지정 경로로 HTTP POST를 수행하며,
     * 실패 시 재시도는 Cloud Tasks 큐 설정(maxAttempts/backoff)에 위임한다.
     * */
    private void enqueue(String path, Object request) {
        try {
            String queuePath = QueueName.of(projectId, location, queue).toString();
            byte[] body = objectMapper.writeValueAsBytes(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                .setUrl(aiServerBaseUrl + path)
                .setHttpMethod(HttpMethod.POST)
                .putHeaders("x-internal-api-key", internalApiKey)
                .putHeaders(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody(ByteString.copyFrom(body))
                .build();

            Task task = Task.newBuilder()
                .setHttpRequest(httpRequest)
                .build();

            cloudTasksClient.createTask(queuePath, task);
        } catch (Exception exception) {
            log.error("Cloud Tasks 등록 실패 path={}", path, exception);
            throw new BaseException(AiErrorCode.AI_SERVER_ERROR);
        }
    }
}
