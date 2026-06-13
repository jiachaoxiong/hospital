package com.hospital.order.service;

import com.hospital.common.R;
import com.hospital.order.entity.Order;
import java.util.List;

/**
 * 订单服务接口 — 定义订单创建、支付、取消、查询等业务操作
 */
public interface OrderService {
    /** 创建订单，返回订单ID */
    R<Long> createOrder(Long userId, Long appointmentId, Double amount);
    /** 支付成功回调处理 */
    void paySuccess(Long orderId);
    /** 取消订单 */
    void cancelOrder(Long orderId);
    /** 查询用户的所有订单 */
    R<List<Order>> listUserOrders(Long userId);
}
