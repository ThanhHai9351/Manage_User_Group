package com.example.springboot_demo.cronjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.springboot_demo.modules.users.reponsitories.BlacklistedTokenRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;

@Service
public class BlacklistTokenClean {

    private static final Logger logger = LoggerFactory.getLogger(BlacklistTokenClean.class);

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int deletedCount = blacklistedTokenRepository.deleteByExpiryDateBefore(currentDateTime);
        logger.info("Cleaned up {} expired tokens from blacklist at {}", deletedCount, currentDateTime);
    }
}
