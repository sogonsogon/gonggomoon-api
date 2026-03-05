package com.sogonsogon.gonggomoon.domain.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "refreshTokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String token;

    protected RefreshToken() {}

    @Builder
    public RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public static RefreshToken createToken(Long userId, String token) {
        return RefreshToken.builder()
            .userId(userId)
            .token(token)
            .build();
    }

    public RefreshToken updateValue(String token) {
        this.token = token;
        return this;
    }

    public void rotate(String token) {
        this.token = token;
    }
}
