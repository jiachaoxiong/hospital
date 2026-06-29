package com.hospital.booking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.booking.entity.Appointment;
import com.hospital.booking.entity.DoctorPatientVO;
import com.hospital.booking.entity.Schedule;
import com.hospital.booking.mapper.AppointmentMapper;
import com.hospital.booking.service.BookingService;
import com.hospital.booking.service.ScheduleService;
import com.hospital.common.BusinessException;
import com.hospital.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 预约服务实现 — 核心：Redis 分布式锁 + 乐观锁双重保障，防止号源超卖
 */
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final ScheduleService scheduleService;
    private final AppointmentMapper appointmentMapper;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    public BookingServiceImpl(ScheduleService scheduleService,
                              AppointmentMapper appointmentMapper,
                              StringRedisTemplate redisTemplate,
                              RestTemplate restTemplate) {
        this.scheduleService = scheduleService;
        this.appointmentMapper = appointmentMapper;
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

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
    public void cancelAppointment(Long userId, Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }
        // IDOR 防护：校验预约归属，防止越权取消他人预约
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作他人的预约");
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

    @Override
    @Transactional
    public void cancelAppointmentInternal(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) return;
        if ("CANCELLED".equals(appointment.getStatus())) return;

        appointment.setStatus("CANCELLED");
        appointmentMapper.updateById(appointment);
        // 归还号源
        scheduleService.update()
                .setSql("remain_quota = remain_quota + 1")
                .eq("id", appointment.getScheduleId())
                .update();
        log.info("预约已内部取消: appointmentId={}", appointmentId);
    }

    @Override
    public void markAsPaid(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!"CANCELLED".equals(appointment.getStatus())) {
            appointment.setStatus("PAID");
            appointmentMapper.updateById(appointment);
            log.info("预约状态更新为PAID: appointmentId={}", appointmentId);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getDoctorIdByUserId(Long userId) {
        try {
            Map<String, Object> resp = restTemplate.getForObject(
                    "http://localhost:8082/doctor/byUserId/" + userId, Map.class);
            if (resp != null && (int) resp.get("code") == 200) {
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                return ((Number) data.get("id")).longValue();
            }
        } catch (Exception e) {
            log.error("调用hospital服务查询医生失败: userId={}, error={}", userId, e.getMessage());
        }
        throw new BusinessException("未找到关联的医生档案");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DoctorPatientVO> getDoctorPatients(Long userId) {
        // 1. 通过userId找到doctorId
        Long doctorId = getDoctorIdByUserId(userId);

        // 2. 查询该医生的有效预约记录（排除已取消的，防止患者取消/管理员删除后医生仍能看到）
        List<Appointment> appointments = appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getDoctorId, doctorId)
                        .ne(Appointment::getStatus, "CANCELLED")
                        .orderByDesc(Appointment::getCreateTime));

        List<DoctorPatientVO> result = new ArrayList<>();
        for (Appointment apt : appointments) {
            DoctorPatientVO vo = new DoctorPatientVO();
            vo.setAppointmentId(apt.getId());
            vo.setStatus(apt.getStatus());

            // 3. 查询排班信息获取医院/科室/日期/时间/医生名
            Schedule schedule = scheduleService.getById(apt.getScheduleId());
            if (schedule != null) {
                vo.setHospitalName(schedule.getHospitalName());
                vo.setDepartmentName(schedule.getDepartmentName());
                vo.setWorkDate(schedule.getWorkDate() != null ? schedule.getWorkDate().toString() : "");
                vo.setTimeSlot(schedule.getStartTime() + " - " + schedule.getEndTime());
                vo.setDoctorName(schedule.getDoctorName());
            }

            // 4. 调用auth服务获取患者姓名和手机号
            try {
                Map<String, Object> authResp = restTemplate.getForObject(
                        "http://localhost:8081/auth/user/" + apt.getUserId(), Map.class);
                if (authResp != null && (int) authResp.get("code") == 200) {
                    Map<String, Object> userData = (Map<String, Object>) authResp.get("data");
                    vo.setPatientName((String) userData.get("name"));
                    vo.setPatientPhone((String) userData.get("phone"));
                }
            } catch (Exception e) {
                log.warn("调用auth服务获取用户信息失败: userId={}, error={}", apt.getUserId(), e.getMessage());
                vo.setPatientName("用户ID:" + apt.getUserId());
                vo.setPatientPhone("");
            }

            result.add(vo);
        }
        return result;
    }
}
