package com.example.springboot_demo.modules.users.controllers;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot_demo.modules.users.request.BlackListTokenRequest;
import com.example.springboot_demo.modules.users.request.LoginRequest;
import com.example.springboot_demo.modules.users.request.RefreshTokenRequest;
import com.example.springboot_demo.modules.users.resources.LoginResouce;
import com.example.springboot_demo.modules.users.resources.RefreshTokenResource;
import com.example.springboot_demo.modules.users.service.interfaces.UserServiceInterface;
import com.example.springboot_demo.resources.ApiResource;
import com.example.springboot_demo.resources.ErrorResource;
import com.example.springboot_demo.resources.MessageResource;

import io.jsonwebtoken.Claims;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.springboot_demo.modules.users.service.impl.BlacklistService;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.springboot_demo.modules.users.entities.RefreshToken;
import com.example.springboot_demo.modules.users.reponsitories.RefreshTokenRepository;
import com.example.springboot_demo.services.JwtService;

@RestController
@RequestMapping("/v1/auth")
@Validated
public class AuthController {

    private final UserServiceInterface userService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public AuthController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest request) {
        Object auth = userService.authenticate(request);

        if (auth instanceof LoginResouce loginResouce) {

            ApiResource<LoginResouce> apiResource = ApiResource.ok(loginResouce, "Login successful");
            return ResponseEntity.ok(apiResource);
        }

        if (auth instanceof ApiResource errorResource) {
            return ResponseEntity.unprocessableEntity().body(errorResource);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @PostMapping("blacklisted_tokens")
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlackListTokenRequest request) {
        try {
            Object result = blacklistService.create(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResource("Failed to add token to blacklist"));
        }
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            BlackListTokenRequest request = new BlackListTokenRequest();
            request.setToken(token);

            blacklistService.create(request);

            ApiResource<Void> apiResource = ApiResource.<Void>builder()
                    .success(true)
                    .message("Logout successful")
                    .status(HttpStatus.OK)
                    .build();

            return ResponseEntity.ok(apiResource);
        } catch (Exception e) {
            ApiResource<Void> apiResource = ApiResource.<Void>builder()
                    .success(false)
                    .message("Network Error!")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.internalServerError().body(apiResource);
        }
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Refresh Token không hợp lệ"));
        }

        Optional<RefreshToken> dbRefreshTokenOptional = refreshTokenRepository.findByRefreshToken(refreshToken);

        if (dbRefreshTokenOptional.isPresent()) {
            RefreshToken dbRefreshToken = dbRefreshTokenOptional.get();
            Long userId = dbRefreshToken.getUserId();
            String email = dbRefreshToken.getUser().getEmail();
            String newToken = jwtService.generateToken(userId, email, null);
            String newRefreshToken = jwtService.generateRefreshToken(userId, email);
            return ResponseEntity.ok(new RefreshTokenResource(newToken, newRefreshToken));
        }

        return ResponseEntity.internalServerError().body(new MessageResource("Network Error!"));
    }
}
