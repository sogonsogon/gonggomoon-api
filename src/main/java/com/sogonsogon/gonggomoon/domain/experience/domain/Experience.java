package com.sogonsogon.gonggomoon.domain.experience.domain;

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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Builder
@Getter
@Entity
@Table(name = "experience")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private String experienceRaw; // pdf 원문 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceType experienceType; // 경험 유형

    @Column(nullable = false)
    private String experienceContent; // 경험 내용

    @Enumerated(EnumType.STRING)
    private ExperienceParticipantRole roleType;

    private int teamSize;

    @Enumerated(EnumType.STRING)
    private ExperienceImpactTier impactTier;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // TODO 중복 코드 제거 필요 & 커스텀 에러로 수정하기
    public static Experience create(
            Long userId,
            String title,
            ExperienceType experienceType,
            String experienceContent,
            LocalDate startDate,
            LocalDate endDate
    ) {
        requireText(title);
        requireNonNull(experienceType);
        requireText(experienceContent);
        validateDateRange(startDate, endDate);

        return Experience.builder()
                .userId(userId)
                .title(title)
                .experienceType(experienceType)
                .experienceContent(experienceContent)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public void update(
            String title,
            ExperienceType experienceType,
            String experienceContent,
            LocalDate startDate,
            LocalDate endDate
    ) {
        requireText(title);
        requireNonNull(experienceType);
        requireText(experienceContent);
        validateDateRange(startDate, endDate);

        this.title = title;
        this.experienceType = experienceType;
        this.experienceContent = experienceContent;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    private static void requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("INVALID_ARGUMENT");
        }
    }

    private static void requireNonNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("INVALID_ARGUMENT");
        }
    }

    private static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("INVALID_ARGUMENT");
        }
    }
}
