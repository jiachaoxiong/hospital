package com.hospital.notify;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 通知服务启动类 — RabbitMQ消费者 + 模拟短信发送
 */
@SpringBootApplication(scanBasePackages = {"com.hospital.notify", "com.hospital.common"})
@EnableDiscoveryClient
@MapperScan("com.hospital.notify.mapper")
public class NotifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotifyApplication.class, args);
    }
}
