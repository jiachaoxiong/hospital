package com.hospital.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 订单服务启动类 — 管理订单创建、支付回调、超时取消功能
 */
@SpringBootApplication(scanBasePackages = {"com.hospital.order", "com.hospital.common"})
@EnableDiscoveryClient
@MapperScan("com.hospital.order.mapper")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
