package com.hospital.order.service;

import com.hospital.common.R;
import com.hospital.order.entity.Order;
import java.util.List;

/**
 * 订单服务接口 — 定义订单创建、支付、取消、查询等业务操作
 */
public interface OrderService {
    /** 创建订单（含就诊信息），返回订单ID */
    R<Long> createOrder(Long userId, Long appointmentId, Double amount,
                       String hospitalName, String deptName, String doctorName,
                       String doctorTitle, String workDate, String timeSlot);
    /** 支付成功回调处理 */
    void paySuccess(Long orderId);
    /** 取消订单 */
    void cancelOrder(Long orderId);
    /** 管理员删除订单（物理删除） */
    void deleteOrder(Long orderId);
    /** 查询用户的所有订单 */
    R<List<Order>> listUserOrders(Long userId);
    /** 管理员分页查询全部订单 */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> listAllOrders(Integer current, Integer size);

    /** 根据ID查询订单详情 */
    R<Order> getOrderById(Long orderId);
}
