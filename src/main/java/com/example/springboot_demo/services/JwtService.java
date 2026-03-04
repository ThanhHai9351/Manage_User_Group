package com.example.springboot_demo.services;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.UUID;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot_demo.config.JwtConfig;
import com.example.springboot_demo.modules.users.entities.RefreshToken;
import com.example.springboot_demo.modules.users.reponsitories.BlacklistedTokenRepository;
import com.example.springboot_demo.modules.users.reponsitories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;

@Service
public class JwtService {

    private static Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final JwtConfig jwtConfig;
    private final Key key;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtConfig.getSecretKey().getBytes()));
    }

    public String generateToken(Long userId, String email, Long expirationTime) {
        Date now = new Date();
        if (expirationTime == null) {
            expirationTime = jwtConfig.getExpirationTime();
        }
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }

    public boolean isTokenFormatValid(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                logger.error("Invalid token format");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to validate token format: {}", e.getMessage());
            return false;
        }
    }

    public boolean isSignatureValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Failed to validate token signature: {}", e.getMessage());
            return false;
        }
    }

    public Key getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(keyBytes));
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            logger.error("Failed to validate token expiration: {}", e.getMessage());
            return true;
        }
    }

    public boolean isIssuerToken(String token) {
        String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
        return tokenIssuer.equals(jwtConfig.getIssuer());
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public boolean isBlacklistedToken(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            LocalDateTime expirationLocalDateTime = refreshToken.getExpiryDate();
            Date expiration = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return expiration.after(new Date());
        } catch (Exception e) {
            logger.error("Failed to validate refresh token expiration: {}", e.getMessage());
            return true;
        }
    }

    public String generateRefreshToken(Long userId, String email) {
        logger.info("Generating refresh token for user: {}", userId);
        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationRefreshTokenTime());

        // String refreshToken = Jwts.builder()
        // .setSubject(String.valueOf(userId))
        // .claim("email", email)
        // .setIssuer(jwtConfig.getIssuer())
        // .setIssuedAt(now)
        // .setExpiration(expiryDate)
        // .signWith(key, SignatureAlgorithm.HS512)
        // .compact();
        String refreshToken = UUID.randomUUID().toString();

        LocalDateTime expiryDateLocalDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken dBRefreshToken = optionalRefreshToken.get();
            dBRefreshToken.setRefreshToken(refreshToken);
            dBRefreshToken.setExpiryDate(expiryDateLocalDateTime);
            refreshTokenRepository.save(dBRefreshToken);
        } else {
            RefreshToken insertToken = new RefreshToken();
            insertToken.setUserId(userId);
            insertToken.setRefreshToken(refreshToken);
            insertToken.setExpiryDate(expiryDateLocalDateTime);
            refreshTokenRepository.save(insertToken);
        }

        return refreshToken;
    }
}
