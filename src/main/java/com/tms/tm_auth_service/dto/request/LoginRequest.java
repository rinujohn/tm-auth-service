package com.tms.tm_auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record LoginRequest(
        @Email
        String email,

        @NotBlank
        String password
) {
}
