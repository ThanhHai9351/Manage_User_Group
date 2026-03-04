package com.example.springboot_demo.modules.users.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.springboot_demo.modules.users.entities.User;
import com.example.springboot_demo.modules.users.reponsitories.UserRepository;
import com.example.springboot_demo.modules.users.resources.UserRescource;
import com.example.springboot_demo.resources.ErrorResource;
import com.example.springboot_demo.resources.ApiResource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            UserRescource userRescource = UserRescource.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();

            ApiResource<UserRescource> apiResource = ApiResource.ok(userRescource, "User fetched successfully");
            return ResponseEntity.ok(apiResource);
        } catch (Exception e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", e.getMessage());
            return ResponseEntity.unprocessableEntity().body(new ErrorResource("Failed to login", errors));
        }
    }
}