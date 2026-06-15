package com.hospital.notify.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置 — 声明通知服务需要的队列、交换机、绑定
 * 即使 Order 服务未启动，Notify 也能独立创建和监听队列
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("hospital.order.exchange");
    }

    @Bean
    public Queue orderPaidQueue() {
        return new Queue("order.paid.queue", true);
    }

    @Bean
    public Binding orderPaidBinding() {
        return BindingBuilder.bind(orderPaidQueue()).to(orderExchange()).with("order.paid");
    }
}
