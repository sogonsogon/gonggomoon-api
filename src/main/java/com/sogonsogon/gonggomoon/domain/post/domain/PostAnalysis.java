package com.sogonsogon.gonggomoon.domain.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "post_analysis")
public class PostAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "title", nullable = false)
    private String title;

    //TODO 요약 내용을 하나로 처리 하는게 맞을까? 제목은 하나의 컬럼으로 저장하는데?
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PostAnalysis() {}

    @Builder
    private PostAnalysis(Long postId, String title, String summary) {
        this.postId = postId;
        this.title = title;
        this.summary = summary;
    }

    public static PostAnalysis create(Long postId, String title, String summary) {
        return PostAnalysis.builder()
                .postId(postId)
                .title(title)
                .summary(summary)
                .build();
    }
}
