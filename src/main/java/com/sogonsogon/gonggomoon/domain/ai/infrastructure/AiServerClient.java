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
import com.sogonsogon.gonggomoon.domain.ai.error.AiErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiServerClient {

    private final CloudTasksClient cloudTasksClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.server.base-url}")
    private String aiServerBaseUrl;

    @Value("${ai.server.internal-api-key}")
    private String internalApiKey;

    @Value("${gcp.cloud-tasks.project-id}")
    private String projectId;

    @Value("${gcp.cloud-tasks.location}")
    private String location;

    @Value("${gcp.cloud-tasks.queue}")
    private String queue;

    /*
    * 경험 추출 요청을 Cloud Tasks 큐에 등록하는 메서드
    * (큐에 등록되면 Cloud Tasks가 AI 서버로 HTTP POST를 비동기 전송한다)
    * */
    public void requestExperienceExtraction(ExperienceExtractionAiServerRequest request) {
        enqueue("/api/v1/jobs/experience-extraction", request);
    }

    /*
     * 포트폴리오 요청을 Cloud Tasks 큐에 등록하는 메서드
     * */
    public void requestPortfolioStrategyGeneration(PortfolioStrategyRequest request) {
        enqueue("/api/v1/jobs/portfolio-strategy-generation", request);
    }

    /*
     * 면접 전략 생성 요청을 Cloud Tasks 큐에 등록하는 메서드
     * */
    public void requestInterviewStrategyGeneration(InterviewStrategyRequest request) {
        enqueue("/api/v1/jobs/interview-strategy-generation", request);
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
