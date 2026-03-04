package com.example.springboot_demo.modules.users.reponsitories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.springboot_demo.modules.users.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
