package com.hospital.booking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 预约服务启动类 — 管理排班查询与挂号预约功能
 */
@SpringBootApplication(scanBasePackages = {"com.hospital.booking", "com.hospital.common"})
@EnableDiscoveryClient
@MapperScan("com.hospital.booking.mapper")
public class BookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }
}
