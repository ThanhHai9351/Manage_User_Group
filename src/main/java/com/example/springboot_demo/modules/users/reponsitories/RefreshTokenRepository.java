package com.example.springboot_demo.modules.users.reponsitories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springboot_demo.modules.users.entities.RefreshToken;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(Long userId);

    int deleteByExpiryDateBefore(LocalDateTime currentDateTime);
}
