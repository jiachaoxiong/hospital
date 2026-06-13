package com.hospital.payment.service;
import com.hospital.common.R;
import java.util.Map;

public interface PaymentService {
    R<Map<String, String>> createPayment(Long orderId, Double amount);
    void paymentCallback(Long orderId, String tradeNo);
}
