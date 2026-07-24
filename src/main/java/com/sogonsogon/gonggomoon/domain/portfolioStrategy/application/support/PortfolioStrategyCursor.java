package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.support;

import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.error.GlobalErrorCode;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;

public record PortfolioStrategyCursor(
        Instant createdAt,
        Long id
) {
    private static final String DELIMITER = "|";

    public static PortfolioStrategyCursor decode(String encodedCursor) {
        if (encodedCursor == null) {
            return null;
        }
        if (encodedCursor.isBlank()) {
            throw invalidCursor();
        }

        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(encodedCursor),
                    StandardCharsets.UTF_8
            );
            String[] values = decoded.split("\\|", -1);
            if (values.length != 2) {
                throw invalidCursor();
            }

            Instant createdAt = Instant.parse(values[0]);
            long id = Long.parseLong(values[1]);
            if (id < 1) {
                throw invalidCursor();
            }

            return new PortfolioStrategyCursor(createdAt, id);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw invalidCursor();
        }
    }

    public String encode() {
        String rawCursor = createdAt + DELIMITER + id;
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }

    private static BaseException invalidCursor() {
        return new BaseException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
}
