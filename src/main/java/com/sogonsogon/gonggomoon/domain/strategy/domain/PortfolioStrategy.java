package com.sogonsogon.gonggomoon.domain.strategy.domain;

import com.sogonsogon.gonggomoon.domain.strategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Builder
@Table(name = "portfolio_strategy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PortfolioStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    private Long industryId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "generated_date", nullable = false, updatable = false)
    private LocalDate generatedDate;

    @Column(name = "experience_total_count", nullable = false)
    private int selectedExperienceCount;

    public static PortfolioStrategy create(
            Long userId,
            JobType jobType,
            Long industryId,
            String resultJson,
            int selectedExperienceCount,
            Instant now,
            LocalDate generatedDate
    ) {
        requireNonNull(userId, PortfolioStrategyErrorCode.USERID_REQUIRED);
        requireNonNull(jobType, PortfolioStrategyErrorCode.JOB_TYPE_REQUIRED);

        // 프로그래밍 오류
        Objects.requireNonNull(now, "now must not be null");
        Objects.requireNonNull(generatedDate, "generatedDate must not be null");

        return PortfolioStrategy.builder()
                .userId(userId)
                .jobType(jobType)
                .industryId(industryId)
                .resultJson(resultJson)
                .selectedExperienceCount(selectedExperienceCount)
                .createdAt(now)
                .generatedDate(generatedDate)
                .build();
    }

    private static void requireNonNull(Object value, BaseErrorCode baseErrorCode) {
        if (value == null) {
            throw new BaseException(baseErrorCode);
        }
    }
}
