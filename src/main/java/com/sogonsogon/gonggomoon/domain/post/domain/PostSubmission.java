package com.sogonsogon.gonggomoon.domain.post.domain;

import com.sogonsogon.gonggomoon.domain.post.error.SubmissionErrorCode;
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
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_submissions")
public class PostSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "platform_id", nullable = false)
    private Long platformId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostSubmissionStatus status;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected PostSubmission() {}

    @Builder
    private PostSubmission(String url, Long userId, Long platformId) {
        validate(url, userId, platformId);
        // baseUrl 검증은 서비스에서
        this.url = url;
        this.userId = userId;
        this.platformId = platformId;
        this.status = PostSubmissionStatus.PENDING;
    }

    public static PostSubmission create(String url, Long userId, Long platformId) {
        return PostSubmission.builder()
                .url(url)
                .userId(userId)
                .platformId(platformId)
                .build();
    }

    private void validate(String url, Long userId, Long platformId) {
        if (!StringUtils.hasText(url)) throw new BaseException(SubmissionErrorCode.INVALID_SUBMISSION);
        if (userId == null) throw new BaseException(SubmissionErrorCode.INVALID_SUBMISSION);
        if (platformId == null) throw new BaseException(SubmissionErrorCode.INVALID_SUBMISSION);
    }
}
