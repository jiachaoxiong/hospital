package com.hospital.booking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.booking.entity.Appointment;
import com.hospital.booking.entity.Schedule;
import com.hospital.booking.mapper.AppointmentMapper;
import com.hospital.booking.mapper.ScheduleMapper;
import com.hospital.booking.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 排班服务实现 — 患者查询可用号源 + 管理员CRUD管理 + 医生查自己的排班
 */
@Slf4j
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    private final RestTemplate restTemplate;
    private final AppointmentMapper appointmentMapper;

    public ScheduleServiceImpl(RestTemplate restTemplate, AppointmentMapper appointmentMapper) {
        this.restTemplate = restTemplate;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public List<Schedule> listAvailable(Long hospitalId, Long departmentId, LocalDate date) {
        return list(new LambdaQueryWrapper<Schedule>()
                .eq(hospitalId != null, Schedule::getHospitalId, hospitalId)
                .eq(departmentId != null, Schedule::getDepartmentId, departmentId)
                .eq(date != null, Schedule::getWorkDate, date)
                .gt(Schedule::getRemainQuota, 0)
                .orderByAsc(Schedule::getStartTime));
    }

    @Override
    public Page<Schedule> pageQuery(Integer current, Integer size, Long hospitalId, Long departmentId) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        if (hospitalId != null) wrapper.eq(Schedule::getHospitalId, hospitalId);
        if (departmentId != null) wrapper.eq(Schedule::getDepartmentId, departmentId);
        wrapper.orderByAsc(Schedule::getWorkDate, Schedule::getStartTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addSchedule(Schedule schedule) {
        if (schedule.getRemainQuota() == null) schedule.setRemainQuota(schedule.getTotalQuota());

        // 自动填充冗余字段：通过 hospital 服务查询医院名、科室名、医生名
        try {
            Map<String, Object> hosResp = restTemplate.getForObject(
                    "http://localhost:8082/hospital/" + schedule.getHospitalId(), Map.class);
            if (hosResp != null && (int) hosResp.get("code") == 200) {
                Map<String, Object> hosData = (Map<String, Object>) hosResp.get("data");
                schedule.setHospitalName((String) hosData.get("name"));
            }
        } catch (Exception e) {
            log.warn("查询医院名称失败: hospitalId={}", schedule.getHospitalId());
        }
        try {
            Map<String, Object> deptResp = restTemplate.getForObject(
                    "http://localhost:8082/department/" + schedule.getDepartmentId(), Map.class);
            if (deptResp != null && (int) deptResp.get("code") == 200) {
                Map<String, Object> deptData = (Map<String, Object>) deptResp.get("data");
                schedule.setDepartmentName((String) deptData.get("name"));
            }
        } catch (Exception e) {
            log.warn("查询科室名称失败: departmentId={}", schedule.getDepartmentId());
        }
        try {
            Map<String, Object> docResp = restTemplate.getForObject(
                    "http://localhost:8082/doctor/" + schedule.getDoctorId(), Map.class);
            if (docResp != null && (int) docResp.get("code") == 200) {
                Map<String, Object> docData = (Map<String, Object>) docResp.get("data");
                schedule.setDoctorName((String) docData.get("name"));
                schedule.setDoctorTitle((String) docData.get("title"));
            }
        } catch (Exception e) {
            log.warn("查询医生名称失败: doctorId={}", schedule.getDoctorId());
        }

        save(schedule);
        log.info("排班创建成功: hospital={}, dept={}, doctor={}, date={}",
                schedule.getHospitalName(), schedule.getDepartmentName(),
                schedule.getDoctorName(), schedule.getWorkDate());
    }

    @Override
    public void updateSchedule(Schedule schedule) {
        updateById(schedule);
    }

    @Override
    public void removeSchedule(Long id) {
        // 级联取消该排班下所有有效预约，防止数据孤岛
        List<com.hospital.booking.entity.Appointment> appointments =
                appointmentMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.hospital.booking.entity.Appointment>()
                                .eq(com.hospital.booking.entity.Appointment::getScheduleId, id)
                                .eq(com.hospital.booking.entity.Appointment::getStatus, "PENDING"));
        for (com.hospital.booking.entity.Appointment apt : appointments) {
            apt.setStatus("CANCELLED");
            appointmentMapper.updateById(apt);
            log.info("排班删除-级联取消预约: appointmentId={}", apt.getId());
        }
        removeById(id);
        log.info("排班已删除: scheduleId={}, 级联取消{}条预约", id, appointments.size());
    }

    @Override
    public List<Schedule> listByDoctorId(Long doctorId) {
        return list(new LambdaQueryWrapper<Schedule>()
                .eq(Schedule::getDoctorId, doctorId)
                .orderByAsc(Schedule::getWorkDate, Schedule::getStartTime));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getDoctorIdByUserId(Long userId) {
        // 调用 hospital 服务查找医生档案
        try {
            Map<String, Object> resp = restTemplate.getForObject(
                    "http://localhost:8082/doctor/byUserId/" + userId,
                    Map.class);
            if (resp != null && (int) resp.get("code") == 200) {
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                return ((Number) data.get("id")).longValue();
            }
        } catch (Exception e) {
            log.error("调用hospital服务查询医生失败: userId={}, error={}", userId, e.getMessage());
        }
        throw new RuntimeException("未找到关联的医生档案，请管理员先在医院管理系统中关联您的医生身份");
    }
}
