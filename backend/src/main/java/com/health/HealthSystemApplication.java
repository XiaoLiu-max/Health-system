//package com.health;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//// 必须加这个注解，Spring Boot 才能识别这是启动类
//@SpringBootApplication
//public class HealthSystemApplication {
//    // 主方法，后端服务的入口
//    public static void main(String[] args) {
//        SpringApplication.run(HealthSystemApplication.class, args);
//    }
//}

package com.health;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 排除数据源自动配置，跳过数据库检查
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class HealthSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthSystemApplication.class, args);
    }
}