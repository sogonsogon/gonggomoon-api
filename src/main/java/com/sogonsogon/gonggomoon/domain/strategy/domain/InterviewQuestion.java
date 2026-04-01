package com.sogonsogon.gonggomoon.domain.strategy.domain;

import com.sogonsogon.gonggomoon.domain.strategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.validation.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "interview_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionLevel questionLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_strategy_id")
    private InterviewStrategy interviewStrategy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static InterviewQuestion create(
            String question,
            QuestionLevel level
    ) {
        ValidationUtils.requireText(question, InterviewStrategyErrorCode.INVALID_INTERVIEW_QUESTION);
        ValidationUtils.requireNonNull(level, InterviewStrategyErrorCode.INVALID_QUESTION_LEVEL);

        return InterviewQuestion.builder()
                .question(question)
                .questionLevel(level)
                .build();
    }

    public void assignStrategy(InterviewStrategy interviewStrategy) {
        this.interviewStrategy = interviewStrategy;
    }
}
