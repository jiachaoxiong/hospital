package com.hospital.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.R;
import com.hospital.order.entity.Order;
import com.hospital.order.mapper.OrderMapper;
import com.hospital.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现 — 订单创建、支付回调、超时取消 + RabbitMQ消息通知
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    public OrderServiceImpl(OrderMapper orderMapper, RabbitTemplate rabbitTemplate, RestTemplate restTemplate) {
        this.orderMapper = orderMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
    }

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

        // 查询订单获取关联的预约ID
        Order existing = orderMapper.selectById(orderId);

        // 调用预约服务更新预约状态为 PAID
        if (existing != null && existing.getAppointmentId() != null) {
            try {
                restTemplate.postForObject(
                        "http://localhost:8083/booking/paid/" + existing.getAppointmentId(),
                        null, String.class);
                log.info("已通知预约服务更新预约状态: appointmentId={}", existing.getAppointmentId());
            } catch (Exception e) {
                log.error("通知预约服务失败: appointmentId={}, error={}",
                        existing.getAppointmentId(), e.getMessage());
            }
        }

        // 发送支付成功消息到 RabbitMQ，通知其他服务（短信等）
        rabbitTemplate.convertAndSend("hospital.order.exchange", "order.paid", orderId);
        log.info("订单支付成功: orderId={}", orderId);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        // IDOR 防护：校验订单归属，防止越权取消他人订单
        Order existing = orderMapper.selectById(orderId);
        if (existing == null) {
            throw new com.hospital.common.BusinessException("订单不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new com.hospital.common.BusinessException(403, "无权操作他人的订单");
        }
        // 已支付/已取消的订单不允许通过患者端取消
        if ("PAID".equals(existing.getStatus())) {
            throw new com.hospital.common.BusinessException("已支付的订单无法取消，请联系医院退款");
        }
        if ("CANCELLED".equals(existing.getStatus())) {
            throw new com.hospital.common.BusinessException("订单已取消，请勿重复操作");
        }
        // 1. 更新订单状态为 CANCELLED
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
        // 2. 级联取消关联的预约，归还号源
        if (existing.getAppointmentId() != null) {
            try {
                restTemplate.postForObject(
                        "http://localhost:8083/booking/internal/cancel/" + existing.getAppointmentId(),
                        null, String.class);
                log.info("已级联取消预约并归还号源: appointmentId={}", existing.getAppointmentId());
            } catch (Exception e) {
                log.error("级联取消预约失败: appointmentId={}, error={}",
                        existing.getAppointmentId(), e.getMessage());
            }
        }
        // 3. 发送取消订单消息到 RabbitMQ，通知其他服务
        rabbitTemplate.convertAndSend("hospital.order.exchange", "order.cancel", orderId);
        log.info("订单已取消: orderId={}, userId={}", orderId, userId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        // 级联取消关联的预约，保证医生端不再看到该患者
        Order existing = orderMapper.selectById(orderId);
        if (existing != null && existing.getAppointmentId() != null) {
            try {
                restTemplate.postForObject(
                        "http://localhost:8083/booking/internal/cancel/" + existing.getAppointmentId(),
                        null, String.class);
                log.info("已级联取消关联预约: appointmentId={}", existing.getAppointmentId());
            } catch (Exception e) {
                log.warn("级联取消预约失败: appointmentId={}, error={}",
                        existing.getAppointmentId(), e.getMessage());
            }
        }
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
    public R<Order> getOrderById(Long userId, String role, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return R.fail("订单不存在");
        }
        // IDOR 防护：仅订单归属人或管理员可查看订单详情
        if (!"ADMIN".equals(role) && !order.getUserId().equals(userId)) {
            return R.fail(403, "无权查看他人的订单");
        }
        return R.ok(order);
    }
}
