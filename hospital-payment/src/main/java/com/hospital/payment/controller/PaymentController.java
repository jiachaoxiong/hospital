package com.hospital.payment.controller;
import com.hospital.common.R;
import com.hospital.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Tag(name = "支付接口")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "发起微信支付（模拟）")
    @PostMapping("/pay")
    public R<Map<String, String>> pay(@RequestParam Long orderId, @RequestParam Double amount) {
        return paymentService.createPayment(orderId, amount);
    }

    @Operation(summary = "支付成功回调")
    @PostMapping("/callback")
    public R<Void> callback(@RequestParam Long orderId, @RequestParam String tradeNo) {
        paymentService.paymentCallback(orderId, tradeNo);
        return R.ok();
    }
}
