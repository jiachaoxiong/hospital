package com.hospital.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动类 — 负责用户注册、登录、JWT 签发
 */
@SpringBootApplication(scanBasePackages = {"com.hospital.auth", "com.hospital.common"})
@EnableDiscoveryClient
@MapperScan("com.hospital.auth.mapper")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
