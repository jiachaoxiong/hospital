package com.hospital.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置 — 声明订单相关的交换机、队列与绑定关系
 */
@Configuration
public class RabbitMQConfig {
    /** 订单业务交换机名称 */
    public static final String ORDER_EXCHANGE = "hospital.order.exchange";
    /** 支付成功队列 */
    public static final String ORDER_PAID_QUEUE = "order.paid.queue";
    /** 订单取消队列 */
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderPaidQueue() {
        return new Queue(ORDER_PAID_QUEUE, true);
    }

    @Bean
    public Queue orderCancelQueue() {
        return new Queue(ORDER_CANCEL_QUEUE, true);
    }

    @Bean
    public Binding orderPaidBinding() {
        return BindingBuilder.bind(orderPaidQueue()).to(orderExchange()).with("order.paid");
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue()).to(orderExchange()).with("order.cancel");
    }
}
