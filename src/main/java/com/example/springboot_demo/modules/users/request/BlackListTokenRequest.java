package com.example.springboot_demo.modules.users.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class BlackListTokenRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
