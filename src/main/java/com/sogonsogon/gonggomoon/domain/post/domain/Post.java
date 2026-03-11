package com.sogonsogon.gonggomoon.domain.post.domain;

import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "platform_id")
    private Long platformId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "experience_level")
    private Integer experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @Column(name = "original_content", nullable = false, columnDefinition = "TEXT")
    private String originalContent;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected Post() {}

}
