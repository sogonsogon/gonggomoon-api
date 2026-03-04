package com.sogonsogon.gonggomoon.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
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
    },
    indexes = {
        @Index(name = "ux_users_public_id", columnList = "public_id", unique = true)
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

    @Column(name = "public_id", nullable = false, updatable = false, unique = true)
    private UUID publicId;

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

    /**
     * 프로필이 변경된 경우에만 업데이트를 수행하는 메서드입니다.
     * 변경 여부를 boolean으로 반환하여, 변경이 발생한 경우에만 DB 업데이트가 일어나도록 할 수 있습니다.
     *
     * @param name 변경된 이름
     * @param profileImageUrl 변경된 프로필 이미지 URL
     * @return 프로필이 변경된 경우 true, 그렇지 않은 경우 false
    * */
    public boolean updateProfile(String name, String profileImageUrl) {
        boolean changed = false;

        // 1) 이름 정규화 + 빈 값 방지
        String newName = normalizeName(name);  // null or cleaned

        // NOTE : this.name이 null이 될 수 없도록 설계되어 있지만, 혹시라도 null이 들어오는 경우를 대비하여 null 체크를 추가합니다.
        if (newName != null && !this.name.equals(newName)) {
            this.name = newName;
            changed = true;
        }

        // 2) 이미지 URL 정규화 + 빈 값 방지
        String newProfileImageUrl = normalizeProfileUrl(profileImageUrl); // null or cleaned

        // NOTE : this.profileImageUrl이 null이 될 수 있으므로, equals 비교 전에 null 체크를 합니다.
        if (newProfileImageUrl != null && !this.profileImageUrl.equals(newProfileImageUrl)) {
            this.profileImageUrl = newProfileImageUrl;
            changed = true;
        }

        return changed;
    }

    private String normalizeName(String name) {
        if (name == null) return null;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return null;

        // "홍길동   입니다" 같은 연속 공백을 하나로
        return trimmed.replaceAll("\\s+", " ");
    }

    private String normalizeProfileUrl(String url) {
        if (url == null) return null;
        String trimmed = url.trim();
        if (trimmed.isEmpty()) return null;

        // ✅ 선택 정책: 프로필 URL이 매번 변동되는 쿼리스트링(?, &)이 붙는 경우가 많으면 제거
        int q = trimmed.indexOf('?');
        return (q >= 0) ? trimmed.substring(0, q) : trimmed;
    }
}