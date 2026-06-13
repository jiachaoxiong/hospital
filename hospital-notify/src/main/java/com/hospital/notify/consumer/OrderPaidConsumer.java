package com.hospital.notify.consumer;

import com.hospital.notify.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单支付成功消费者 — 监听 order.paid.queue 队列，支付成功后发送短信通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidConsumer {
    private final SmsService smsService;

    @RabbitListener(queues = "order.paid.queue")
    public void onOrderPaid(Long orderId) {
        log.info("收到支付成功消息: orderId={}", orderId);
        smsService.sendSms("138****0000", "您的订单（" + orderId + "）已支付成功，请在预约时间前往医院就诊。");
    }
}
