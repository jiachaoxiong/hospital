package com.hospital.booking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.booking.entity.Schedule;
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

    public ScheduleServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
    public void addSchedule(Schedule schedule) {
        if (schedule.getRemainQuota() == null) schedule.setRemainQuota(schedule.getTotalQuota());
        save(schedule);
    }

    @Override
    public void updateSchedule(Schedule schedule) {
        updateById(schedule);
    }

    @Override
    public void removeSchedule(Long id) {
        removeById(id);
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
