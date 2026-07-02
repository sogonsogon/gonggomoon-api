package com.sogonsogon.gonggomoon.domain.post.domain;

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

    @Column(length = 2083, name = "url")
    private String url;

    //TODO STATUS 이름도 좀 더 생각해야 할듯
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // 유저 ID
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "analysis_id")
    private Long analysisId;

    protected Post() {}

    @Builder
    private Post(String url, Long userId) {
        this.url = url;
        this.createdBy = userId;
        this.status = PostStatus.PENDING;
    }

    public static Post create(String url, Long userId) {
        return Post.builder()
                .url(url)
                .userId(userId)
                .build();
    }

    public void success(Long analysisId) {
        this.status = PostStatus.SUCCESS;
        this.analysisId = analysisId;
    }

    public void failed() {
        this.status = PostStatus.FAILED;
    }
}
