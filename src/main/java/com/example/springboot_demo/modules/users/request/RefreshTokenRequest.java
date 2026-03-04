package com.example.springboot_demo.modules.users.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
