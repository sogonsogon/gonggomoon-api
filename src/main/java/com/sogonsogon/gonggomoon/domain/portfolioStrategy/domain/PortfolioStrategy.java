package com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.validation.ValidationUtils;
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

    /**
     * AI 서버에서 생성된 전략 결과 (nullable)
     */
    @Column(columnDefinition = "TEXT")
    private String resultJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    private Long industryId;

    /**
     * 전략 생성 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PortfolioStrategyGenerateStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "generated_date", nullable = false, updatable = false)
    private LocalDate generatedDate;

    @Column(name = "experience_total_count", nullable = false)
    private int selectedExperienceCount;

    /**
     * 기본 포트폴리오 전략 데이터를 생성합니다.
     * - resultJson 없이 생성하는 메서드.
     * */
    public static PortfolioStrategy create(
            Long userId,
            JobType jobType,
            Long industryId,
            int selectedExperienceCount,
            Instant now,
            LocalDate generatedDate
    ) {
        ValidationUtils.requireNonNull(userId, PortfolioStrategyErrorCode.USERID_REQUIRED);
        ValidationUtils.requireNonNull(jobType, PortfolioStrategyErrorCode.JOB_TYPE_REQUIRED);

        // 프로그래밍 오류
        Objects.requireNonNull(now, "now must not be null");
        Objects.requireNonNull(generatedDate, "generatedDate must not be null");

        return PortfolioStrategy.builder()
                .userId(userId)
                .jobType(jobType)
                .industryId(industryId)
                .selectedExperienceCount(selectedExperienceCount)
                .createdAt(now)
                .generatedDate(generatedDate)
                .status(PortfolioStrategyGenerateStatus.PROCESSING)
                .build();
    }

    public void updateStatus(PortfolioStrategyGenerateStatus status) {
        this.status = status;
    }

    public void addResult(String resultJson) {
        if (resultJson == null || resultJson.isEmpty()) {
            throw new BaseException(PortfolioStrategyErrorCode.RESULT_JSON_EMPTY);
        }
        this.resultJson = resultJson;
        this.status = PortfolioStrategyGenerateStatus.READY;
    }
}
