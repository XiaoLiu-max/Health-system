package com.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.health.feign")
public class HealthSystemApplication {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(HealthSystemApplication.class, args);
    }
}