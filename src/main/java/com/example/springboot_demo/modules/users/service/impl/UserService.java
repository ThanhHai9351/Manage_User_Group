package com.example.springboot_demo.modules.users.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springboot_demo.config.JwtConfig;
import com.example.springboot_demo.modules.users.entities.User;
import com.example.springboot_demo.modules.users.reponsitories.UserRepository;
import com.example.springboot_demo.modules.users.request.LoginRequest;
import com.example.springboot_demo.modules.users.resources.LoginResouce;
import com.example.springboot_demo.modules.users.resources.UserRescource;
import com.example.springboot_demo.modules.users.service.interfaces.UserServiceInterface;
import com.example.springboot_demo.resources.ApiResource;
import com.example.springboot_demo.services.BaseService;
import com.example.springboot_demo.services.JwtService;

@Service
public class UserService implements UserServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public Object authenticate(LoginRequest request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();

            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            String token = jwtService.generateToken(user.getId(), user.getEmail(),
                    jwtConfig.getDefaultExpirationTime()); // 5 minutes

            String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

            return new LoginResouce(token, refreshToken,
                    new UserRescource(user.getId(), user.getEmail(), user.getName()));
        } catch (Exception e) {
            logger.error("Failed to login: {}", e.getMessage());
            return ApiResource.error("AUTH_ERROR", e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
