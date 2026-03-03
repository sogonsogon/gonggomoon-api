package com.sogonsogon.gonggomoon.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false, length = 255)
    private String email; // Unique (암호화 여부는 별도 컨버터/컬럼 설계에 따라)

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role; // ADMIN, USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status; // active, sleep, withdrawn

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // 가입일(UTC)

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt; // 수정일(Nullable)

    @Column
    private String profileImageUrl; // Nullable

    public enum Role {
        ADMIN, USER
    }

    public enum Status {
        ACTIVE, SLEEP, WITHDRAWN
    }
}