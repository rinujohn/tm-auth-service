package com.tms.tm_auth_service.dto.response;

public record RefreshTokenResponse(
        String jwtAccessToken,
        String refreshToken
) {
}
