package com.example.springboot_demo.cronjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.springboot_demo.modules.users.reponsitories.RefreshTokenRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;

@Service
public class RefreshTokenClean {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenClean.class);

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(currentDateTime);
        logger.info("Cleaned up {} expired refresh tokens at {}", deletedCount, currentDateTime);
    }
}
