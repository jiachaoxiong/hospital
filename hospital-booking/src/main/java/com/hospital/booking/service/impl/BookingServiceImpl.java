package com.hospital.booking.service.impl;

import com.hospital.booking.entity.Appointment;
import com.hospital.booking.entity.Schedule;
import com.hospital.booking.mapper.AppointmentMapper;
import com.hospital.booking.service.BookingService;
import com.hospital.booking.service.ScheduleService;
import com.hospital.common.BusinessException;
import com.hospital.common.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

/**
 * 预约服务实现 — 核心：Redis 分布式锁 + 乐观锁双重保障，防止号源超卖
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ScheduleService scheduleService;
    private final AppointmentMapper appointmentMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public R<Long> createAppointment(Long userId, Long scheduleId) {
        // Redis 分布式锁：防止同一号源并发抢购
        String lockKey = "booking:lock:" + scheduleId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, String.valueOf(userId), 5, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new BusinessException("该号源正在被其他人抢购，请稍后再试");
        }
        try {
            // 查询号源
            Schedule schedule = scheduleService.getById(scheduleId);
            if (schedule == null || schedule.getRemainQuota() <= 0) {
                throw new BusinessException("号源已约满");
            }
            // 乐观锁扣减号源：在 WHERE 中判断 remain_quota > 0，防止超卖
            boolean success = scheduleService.update()
                    .setSql("remain_quota = remain_quota - 1")
                    .eq("id", scheduleId)
                    .gt("remain_quota", 0)
                    .update();
            if (!success) {
                throw new BusinessException("号源已被抢完");
            }
            // 插入预约记录
            Appointment appointment = new Appointment();
            appointment.setUserId(userId);
            appointment.setScheduleId(scheduleId);
            appointment.setHospitalId(schedule.getHospitalId());
            appointment.setDepartmentId(schedule.getDepartmentId());
            appointment.setDoctorId(schedule.getDoctorId());
            appointment.setStatus("PENDING");
            appointmentMapper.insert(appointment);
            log.info("预约成功: userId={}, appointmentId={}", userId, appointment.getId());
            return R.ok(appointment.getId());
        } finally {
            // 释放分布式锁
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }
        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new BusinessException("预约已取消");
        }
        // 更新预约状态为已取消
        appointment.setStatus("CANCELLED");
        appointmentMapper.updateById(appointment);
        // 归还号源
        scheduleService.update()
                .setSql("remain_quota = remain_quota + 1")
                .eq("id", appointment.getScheduleId())
                .update();
        log.info("预约已取消: appointmentId={}", appointmentId);
    }
}
