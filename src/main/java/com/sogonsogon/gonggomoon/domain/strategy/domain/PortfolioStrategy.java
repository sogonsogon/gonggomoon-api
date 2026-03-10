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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

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

    @Enumerated(EnumType.STRING)
    private IndustryType industryType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "experience_total_count", nullable = false)
    private int selectedExperienceCount;

    public static PortfolioStrategy create(
            Long userId,
            JobType jobType,
            IndustryType industryType,
            String resultJson,
            int selectedExperienceCount
    ) {
        requireNonNull(userId, PortfolioStrategyErrorCode.USERID_REQUIRED);
        requireNonNull(jobType, PortfolioStrategyErrorCode.JOB_TYPE_REQUIRED);

        return PortfolioStrategy.builder()
                .userId(userId)
                .jobType(jobType)
                .industryType(industryType)
                .resultJson(resultJson)
                .selectedExperienceCount(selectedExperienceCount)
                .build();
    }

    private static void requireNonNull(Object value, BaseErrorCode baseErrorCode) {
        if (value == null) {
            throw new BaseException(baseErrorCode);
        }
    }
}
