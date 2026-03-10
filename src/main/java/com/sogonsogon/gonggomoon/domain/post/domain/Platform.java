package com.sogonsogon.gonggomoon.domain.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

//TODO 1. 사람인, 원티드, 잡코리아, 자소설
//URL검증, 플랫폼 관리 API 등 플랫폼 자체 비즈니스 로직이 생길 가능성이 높고
//나중에 크롤링 기능이 붙으면 플랫폼을 중심으로 기능이 확장될 가능성이 커서 분리해야 할 듯? >> V2
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_platforms")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_url")
    private String baseUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Platform() {}
}
