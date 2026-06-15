package com.hospital.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.R;
import com.hospital.order.entity.Order;
import com.hospital.order.mapper.OrderMapper;
import com.hospital.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现 — 订单创建、支付回调、超时取消 + RabbitMQ消息通知
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public R<Long> createOrder(Long userId, Long appointmentId, Double amount,
                               String hospitalName, String deptName, String doctorName,
                               String doctorTitle, String workDate, String timeSlot) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAppointmentId(appointmentId);
        order.setAmount(amount);
        order.setStatus("PENDING");
        order.setHospitalName(hospitalName);
        order.setDeptName(deptName);
        order.setDoctorName(doctorName);
        order.setDoctorTitle(doctorTitle);
        order.setWorkDate(workDate);
        order.setTimeSlot(timeSlot);
        orderMapper.insert(order);
        log.info("订单创建成功: orderId={}, userId={}, hospital={}, doctor={}",
            order.getId(), userId, hospitalName, doctorName);
        return R.ok(order.getId());
    }

    @Override
    public void paySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        // 发送支付成功消息到 RabbitMQ，通知其他服务更新预约状态
        rabbitTemplate.convertAndSend("hospital.order.exchange", "order.paid", orderId);
        log.info("订单支付成功: orderId={}", orderId);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
        // 发送取消订单消息到 RabbitMQ，通知其他服务释放号源
        rabbitTemplate.convertAndSend("hospital.order.exchange", "order.cancel", orderId);
        log.info("订单已取消: orderId={}", orderId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderMapper.deleteById(orderId);
        log.info("订单已删除: orderId={}", orderId);
    }

    @Override
    public R<List<Order>> listUserOrders(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);
        List<Order> orders = orderMapper.selectList(wrapper);
        return R.ok(orders);
    }

    @Override
    public Page<Order> listAllOrders(Integer current, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public R<Order> getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.ok(order);
    }
}
