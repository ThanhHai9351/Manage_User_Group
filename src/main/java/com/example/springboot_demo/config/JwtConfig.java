package com.example.springboot_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.issuer}")
    public String issuer;

    @Value("${jwt.expirationRefreshToken}")
    private long expirationRefreshTokenTime;

    @Value("${jwt.defaultExpiration}")
    private long defaultExpirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getIssuer() {
        return issuer;
    }

    public long getExpirationRefreshTokenTime() {
        return expirationRefreshTokenTime;
    }

    public long getDefaultExpirationTime() {
        return defaultExpirationTime;
    }
}
