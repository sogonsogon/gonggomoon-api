package com.sogonsogon.gonggomoon.global.post;

import com.sogonsogon.gonggomoon.domain.post.dto.request.TavilyExtractRequest;
import com.sogonsogon.gonggomoon.domain.post.dto.response.TavilyExtractResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class TavilyClient {

    private final RestClient restClient;
    private final String apiKey;

    public TavilyClient(
            RestClient.Builder builder,
            @Value("${tavily.base-url}") String baseUrl,
            @Value("${tavily.api-key}") String apiKey
    ) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public TavilyExtractResponse extract(String url) {
        TavilyExtractRequest request = new TavilyExtractRequest(
                apiKey,
                List.of(url),
                "advanced",
                false,
                "markdown"
        );

        return restClient.post()
                .uri("/extract")
                .body(request)
                .retrieve()
                .body(TavilyExtractResponse.class);
    }
}
