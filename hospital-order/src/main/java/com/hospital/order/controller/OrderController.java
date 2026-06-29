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

    @Operation(summary = "创建订单（含就诊信息）")
    @PostMapping("/create")
    public R<Long> create(@RequestHeader("X-User-Id") Long userId,
                          @RequestParam Long appointmentId,
                          @RequestParam(defaultValue = "0") Double amount,
                          @RequestParam(defaultValue = "") String hospitalName,
                          @RequestParam(defaultValue = "") String deptName,
                          @RequestParam(defaultValue = "") String doctorName,
                          @RequestParam(defaultValue = "") String doctorTitle,
                          @RequestParam(defaultValue = "") String workDate,
                          @RequestParam(defaultValue = "") String timeSlot) {
        return orderService.createOrder(userId, appointmentId, amount,
            hospitalName, deptName, doctorName, doctorTitle, workDate, timeSlot);
    }

    @Operation(summary = "支付成功回调")
    @PostMapping("/pay-success/{id}")
    public R<Void> paySuccess(@PathVariable Long id) {
        orderService.paySuccess(id);
        return R.ok();
    }

    @Operation(summary = "取消订单（仅订单归属人可操作）")
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@RequestHeader("X-User-Id") Long userId,
                          @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return R.ok();
    }

    @Operation(summary = "管理员删除订单")
    @DeleteMapping("/{id}")
    public R<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return R.ok();
    }

    @Operation(summary = "我的订单")
    @GetMapping("/my")
    public R<List<Order>> myOrders(@RequestHeader("X-User-Id") Long userId) {
        return orderService.listUserOrders(userId);
    }

    @Operation(summary = "管理员分页查询全部订单")
    @GetMapping("/list")
    public R<com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order>> listAll(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.ok(orderService.listAllOrders(current, size));
    }

    @Operation(summary = "订单详情（仅订单归属人或管理员可查看）")
    @GetMapping("/{id}")
    public R<Order> detail(@RequestHeader("X-User-Id") Long userId,
                           @RequestHeader("X-User-Role") String role,
                           @PathVariable Long id) {
        return orderService.getOrderById(userId, role, id);
    }
}
