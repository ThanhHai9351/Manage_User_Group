package com.example.springboot_demo.modules.users.reponsitories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springboot_demo.modules.users.entities.BlacklistedToken;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);

    int deleteByExpiryDateBefore(LocalDateTime currentDateTime);
}
