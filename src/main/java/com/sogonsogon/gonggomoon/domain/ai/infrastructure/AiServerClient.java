package com.sogonsogon.gonggomoon.domain.ai.infrastructure;


import com.sogonsogon.gonggomoon.domain.ai.error.AiServerErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AiServerClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.server.base-url}")
    private String aiServerBaseUrl;

    @Value("${ai.server.internal-api-key}")
    private String internalApiKey;

    /*
    * 경험 추출 요청을 AI 서버로 전송하는 메서드
    * */
    public void requestExperienceExtraction(Long extractedExperienceId) {
        WebClient webClient = webClientBuilder
            .baseUrl(aiServerBaseUrl)
            .build();

        // 응답값이 없음 202 Accepted
        webClient.post()
            .uri("/api/v1/jobs/experience-extraction")
            .header("x-internal-api-key", internalApiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .bodyValue(extractedExperienceId)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(
                        new BaseException(AiServerErrorCode.AI_SERVER_ERROR)
                    ))
            )
            .toBodilessEntity()
            .block();
    }
}