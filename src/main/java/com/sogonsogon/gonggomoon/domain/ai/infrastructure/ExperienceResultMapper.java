package com.sogonsogon.gonggomoon.domain.ai.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExperienceItem;
import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.error.ExperienceResultMapperError;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExperienceResultMapper {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public Experiences toExperiences(JsonNode resultNode) {
        JsonNode experiencesNode = resultNode.path("analysis").path("experiences");

        if (!experiencesNode.isArray()) {
            throw new BaseException(ExperienceResultMapperError.EXPERIENCES_ONLY_ARRAY);
        }

        List<ExperienceItem> items = new ArrayList<>();

        for (JsonNode node : experiencesNode) {
            items.add(toExperienceItem(node));
        }

        return Experiences.of(items);
    }

    private ExperienceItem toExperienceItem(JsonNode node) {
        return ExperienceItem.builder()
            .title(getText(node, "title"))
            .experienceContent(getText(node, "experienceContent"))
            .experienceType(parseExperienceType(node.get("experienceType")))
            .startDate(parseYearMonth(node.get("startDate")))
            .endDate(parseYearMonth(node.get("endDate")))
            .build();
    }

    private String getText(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            return null;
        }

        String text = value.asText();
        return text == null || text.isBlank() ? null : text.trim();
    }

    private ExperienceType parseExperienceType(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        String value = node.asText();
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return ExperienceType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BaseException(ExperienceResultMapperError.INVALID_EXPERIENCE_TYPE);
        }
    }

    private YearMonth parseYearMonth(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        String value = node.asText();
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return YearMonth.parse(value.trim(), YEAR_MONTH_FORMATTER);

            // TODO : CustomException으로 변경하기
        } catch (DateTimeParseException e) {
            throw new BaseException(ExperienceResultMapperError.DATE_FORMAT_ERROR);
        }
    }
}