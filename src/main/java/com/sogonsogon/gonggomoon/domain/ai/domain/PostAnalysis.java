package com.sogonsogon.gonggomoon.domain.ai.domain;

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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.dialect.PostgreSQLDialect;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_analysis")
public class PostAnalysis {

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

    @Column(name = "url", columnDefinition = "TEXT", unique = true)
    private String url;

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
    private PostAnalysis(String url, String title, String summary) {
        this.url = url;
        this.title = title;
        this.summary = summary;
    }

    public static PostAnalysis create(String url, String title, String summary) {
        return PostAnalysis.builder()
                .url(url)
                .title(title)
                .summary(summary)
                .build();
    }

}
