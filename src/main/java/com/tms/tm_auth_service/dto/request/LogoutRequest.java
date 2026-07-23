package com.tms.tm_auth_service.dto.request;


import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken

) {
}
