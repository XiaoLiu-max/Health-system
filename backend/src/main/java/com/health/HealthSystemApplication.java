package com.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.health.mapper") // 关键：告诉MyBatis去这个包里扫描Mapper接口
public class HealthSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthSystemApplication.class, args);
    }
}