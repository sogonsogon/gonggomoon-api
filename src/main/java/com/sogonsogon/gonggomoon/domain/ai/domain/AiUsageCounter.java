package com.sogonsogon.gonggomoon.domain.ai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "ai_usage_counter",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ai_usage_counter_user_type_week",
            columnNames = {"user_id", "usage_type", "week_start_date"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiUsageCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false, length = 40)
    private AiUsageType usageType;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private AiUsageCounter(Long userId, AiUsageType usageType, LocalDate weekStartDate, Instant now) {
        this.userId = userId;
        this.usageType = usageType;
        this.weekStartDate = weekStartDate;
        this.usageCount = 0;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static AiUsageCounter create(Long userId, AiUsageType usageType, LocalDate weekStartDate) {
        return new AiUsageCounter(userId, usageType, weekStartDate, Instant.now());
    }
}
