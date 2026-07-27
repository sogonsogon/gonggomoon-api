package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import java.util.UUID;

public record PostResponse(
        UUID postAnalysisId,
        String url,
        String title,
        JsonNode summary
) {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static PostResponse of(PostAnalysis analysis) throws JsonProcessingException {
            JsonNode convertedSummary = objectMapper.readTree(analysis.getSummary());
            return new PostResponse(
                    analysis.getPublicId(),
                    analysis.getUrl(),
                    analysis.getTitle(),
                    convertedSummary
            );
        }
    }
