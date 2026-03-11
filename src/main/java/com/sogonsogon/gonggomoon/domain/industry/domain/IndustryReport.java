package com.sogonsogon.gonggomoon.domain.industry.domain;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "industry_reports")
public class IndustryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "industry_category_id", nullable = false)
    private Long industryCategoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IndustryReportStatus status;

    @Column(name = "report_year")
    private Integer reportYear;

    @Column(name = "competition", columnDefinition = "TEXT")
    private String competition;

    @Column(name = "market_size", columnDefinition = "TEXT")
    private String marketSize;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trend", columnDefinition = "jsonb")
    private List<String> trend;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "regulation", columnDefinition = "jsonb")
    private List<String> regulation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "keyword", columnDefinition = "jsonb")
    private List<String> keyword;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hiring", columnDefinition = "jsonb")
    private List<String> hiring;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "investment", columnDefinition = "jsonb")
    private List<String> investment;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long publishedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected IndustryReport() {}
}
