package com.hospital.hospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 医院服务启动类 — 管理医院、科室、医生信息
 */
@SpringBootApplication(scanBasePackages = {"com.hospital.hospital", "com.hospital.common"})
@EnableDiscoveryClient
@MapperScan("com.hospital.hospital.mapper")
public class HospitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
    }
}
