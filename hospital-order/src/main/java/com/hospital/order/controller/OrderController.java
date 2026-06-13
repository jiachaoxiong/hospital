package com.hospital.order.controller;

import com.hospital.common.R;
import com.hospital.order.entity.Order;
import com.hospital.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器 — 提供订单创建、支付、取消、查询等 REST API
 */
@Tag(name = "订单接口")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public R<Long> create(@RequestHeader("X-User-Id") Long userId,
                          @RequestParam Long appointmentId,
                          @RequestParam(defaultValue = "0") Double amount) {
        return orderService.createOrder(userId, appointmentId, amount);
    }

    @Operation(summary = "支付成功回调")
    @PostMapping("/pay-success/{id}")
    public R<Void> paySuccess(@PathVariable Long id) {
        orderService.paySuccess(id);
        return R.ok();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return R.ok();
    }

    @Operation(summary = "我的订单")
    @GetMapping("/my")
    public R<List<Order>> myOrders(@RequestHeader("X-User-Id") Long userId) {
        return orderService.listUserOrders(userId);
    }
}
