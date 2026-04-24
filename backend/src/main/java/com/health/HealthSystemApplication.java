package com.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 保留定时任务注解
@MapperScan("com.health.mapper") // 保留 MyBatis Mapper 扫描
public class HealthSystemApplication {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(HealthSystemApplication.class, args);
    }
}