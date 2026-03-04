package com.example.springboot_demo.modules.users.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_catalouge_id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String image;
    private Integer age;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @PrePersist
    private void onCreate() {
        this.created_at = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updated_at = LocalDateTime.now();
    }

    public Long getUserCatalougeId() {
        return user_catalouge_id;
    }

    public void setUserCatalougeId(Long user_catalouge_id) {
        this.user_catalouge_id = user_catalouge_id;
    }
}
