package com.example.springboot_demo.modules.users.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.example.springboot_demo.modules.users.reponsitories.BlacklistedTokenRepository;
import com.example.springboot_demo.modules.users.entities.BlacklistedToken;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.springboot_demo.modules.users.request.BlackListTokenRequest;
import com.example.springboot_demo.resources.ApiResource;
import com.example.springboot_demo.resources.MessageResource;
import com.example.springboot_demo.services.JwtService;

import io.jsonwebtoken.Claims;
import java.time.ZoneId;

@Service
public class BlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(BlacklistService.class);

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtService jwtService;

    public Object create(BlackListTokenRequest request) {
        try {
            if (blacklistedTokenRepository.existsByToken(request.getToken())) {
                return ApiResource.error("TOKEN_ALREADY_BLACKLISTED", "Token already blacklisted",
                        HttpStatus.BAD_REQUEST);
            }

            Claims claims = jwtService.getAllClaimsFromToken(request.getToken());
            Long userId = Long.valueOf(claims.getSubject());

            Date expiryDate = claims.getExpiration();

            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setUserId(userId);
            blacklistedToken.setToken(request.getToken());
            blacklistedToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            blacklistedTokenRepository.save(blacklistedToken);
            logger.info("Token blacklisted successfully");
            return new MessageResource("Token blacklisted successfully");
        } catch (Exception e) {
            return new MessageResource("Failed to blacklist token: " + e.getMessage());
        }
    }
}
