package com.sogonsogon.gonggomoon.domain.auth.api.dto;

public record ReissuanceResponse(
    String grantType,
    String accessToken,
    String refreshToken
) {
}