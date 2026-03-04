package com.example.springboot_demo.databases.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot_demo.modules.users.entities.User;
import com.example.springboot_demo.modules.users.reponsitories.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (isTableEmpty()) {
            String password = passwordEncoder.encode("123456");
            // entityManager.createNativeQuery(
            // "INSERT INTO users (name, email, password, phone, address, image,
            // user_catalouge_id) VALUES (?, ?, ?, ?, ?, ?, ?)")
            // .setParameter(1, "John Doe")
            // .setParameter(2, "john.doe@example.com")
            // .setParameter(3, password)
            // .setParameter(4, "1234567890")
            // .setParameter(5, "1234567890")
            // .setParameter(6, "1234567890")
            // .setParameter(7, 1)
            // .executeUpdate();
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword(password);
            user.setPhone("1234567890");
            user.setAddress("1234567890");
            user.setImage("1234567890");
            user.setUserCatalougeId(1L);
            userRepository.save(user);
            // entityManager.persist(user);
            System.out.println("DatabaseSeeder is running...");
        }
    }

    private boolean isTableEmpty() {
        Long count = ((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM users").getSingleResult())
                .longValue();
        return count == 0;
    }
}
