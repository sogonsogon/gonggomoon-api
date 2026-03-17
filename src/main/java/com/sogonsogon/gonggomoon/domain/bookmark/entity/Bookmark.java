package com.sogonsogon.gonggomoon.domain.bookmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "bookmarks",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_post",
                columnNames = {"user_id", "post_id"}
        )
        })
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Bookmark() {}

    @Builder
    private Bookmark(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public static Bookmark create(Long userId, Long postId) {
        return Bookmark.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }
}
