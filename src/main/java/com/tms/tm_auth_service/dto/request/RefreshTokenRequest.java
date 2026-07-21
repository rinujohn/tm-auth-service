package com.tms.tm_auth_service.dto.request;


import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken

) {
}
