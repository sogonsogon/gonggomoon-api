package com.sogonsogon.gonggomoon.domain.ai.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "extracted_experiences")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtractedExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 요청한 유저 ID User 엔티티와 연관관계로 묶을 수도 있지만, 우선 단순 ID 참조로 두면 AI/배치성 도메인에서는 더 가볍게 운영 가능
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 참조한 파일 ID
     */
    @Column(name = "file_asset_id", nullable = false)
    private List<Long> fileAssetIds;

    /**
     * 경험 추출 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExtractionStatus status;

    /**
     * 생성 시각 (UTC)
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 업데이트 시각 (UTC)
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * 추출된 경험 목록 jsonb
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experiences", columnDefinition = "jsonb")
    private Experiences experiences;

    @Builder
    private ExtractedExperience(
        Long userId,
        List<Long> fileAssetIds,
        ExtractionStatus status
    ) {
        this.userId = userId;
        this.fileAssetIds = fileAssetIds;
        this.status = status;
    }

    public static ExtractedExperience create(Long userId, List<Long> fileAssetIds) {
        return ExtractedExperience.builder()
            .userId(userId)
            .fileAssetIds(fileAssetIds)
            .status(ExtractionStatus.PROCESSING)
            .build();
    }

    public void updateExperiences(Experiences experiences) {
        this.experiences = experiences;
    }

    public void updateStatus(ExtractionStatus newStatus) {
        this.status = newStatus;
    }
}
