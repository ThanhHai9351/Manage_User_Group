package com.example.springboot_demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/")
public class Hello {
    @GetMapping("health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
