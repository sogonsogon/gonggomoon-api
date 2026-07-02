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

    @Column(name = "post_url", columnDefinition = "TEXT", unique = true)
    private String postUrl;

    @Column(name = "title")
    private String title;

    //TODO 요약 내용을 하나로 처리?
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PostAnalysis() {}

    @Builder
    private PostAnalysis(String postUrl, String title, String summary) {
        this.postUrl = postUrl;
        this.title = title;
        this.summary = summary;
    }

    public static PostAnalysis create(String postUrl, String title, String summary) {
        return PostAnalysis.builder()
                .postUrl(postUrl)
                .title(title)
                .summary(summary)
                .build();
    }
}
