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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.dialect.PostgreSQLDialect;

import java.time.Instant;
import java.util.UUID;

/**
 * 유저 요청 이력
 */
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true)
    @ColumnDefault("random_uuid()")
    @DialectOverride.ColumnDefault(
            dialect = PostgreSQLDialect.class,
            override = @ColumnDefault("gen_random_uuid()")
    )
    private UUID publicId = UUID.randomUUID();

    @Column(length = 2048, name = "url")
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

    @Column(name = "file_asset_id")
    private Long fileAssetId;

    protected Post() {}

    @Builder
    private Post(String url, Long userId, PostStatus status, Long analysisId, Long fileAssetId) {
        this.url = url;
        this.createdBy = userId;
        this.status = status;
        this.analysisId = analysisId;
        this.fileAssetId = fileAssetId;
    }

    public static Post create(String url, Long userId, Long fileAssetId) {
        return Post.builder()
                .url(url)
                .userId(userId)
                .status(PostStatus.PENDING)
                .fileAssetId(fileAssetId)
                .build();
    }

    public static Post createFromCache(String url, Long userId, Long analysisId) {
        return Post.builder()
                .url(url)
                .userId(userId)
                .status(PostStatus.SUCCESS)
                .analysisId(analysisId)
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
