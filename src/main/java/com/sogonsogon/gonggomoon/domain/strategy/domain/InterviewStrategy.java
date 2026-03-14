package com.sogonsogon.gonggomoon.domain.strategy.domain;

import com.sogonsogon.gonggomoon.domain.strategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 면접 전략 질문 세트와 질문은 1:N 관계입니다.
 */
@Builder
@Entity
@Getter
@Table(name = "interview_strategy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class InterviewStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "file_asset_id", nullable = false)
    private Long fileAssetId;

    @Builder.Default
    @OneToMany(mappedBy = "interviewStrategy",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<InterviewQuestion> questions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "generated_date", nullable = false, updatable = false)
    private LocalDate generatedDate;

    public static InterviewStrategy create(
            Long userId,
            Long fileAssetId,
            Instant now,
            LocalDate generatedDate
    ) {
        requireNonNull(userId, InterviewStrategyErrorCode.USER_ID_REQUIRED);
        requireNonNull(fileAssetId, InterviewStrategyErrorCode.FILE_ASSET_ID_REQUIRED);

        // 프로그래밍 오류
        Objects.requireNonNull(now, "now must not be null");
        Objects.requireNonNull(generatedDate, "generatedDate must not be null");

        return InterviewStrategy.builder()
                .userId(userId)
                .fileAssetId(fileAssetId)
                .createdAt(now)
                .generatedDate(generatedDate)
                .build();
    }

    public void addQuestion(InterviewQuestion question) {
        this.questions.add(question);
        question.assignStrategy(this);
    }
    public void addQuestions(List<InterviewQuestion> questions) {
        questions.forEach(this::addQuestion);
    }

    private static void requireNonNull(Object value, BaseErrorCode baseErrorCode) {
        if (value == null) {
            throw new BaseException(baseErrorCode);
        }
    }
}
