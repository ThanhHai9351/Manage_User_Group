package com.example.springboot_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.springboot_demo.modules.*")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
