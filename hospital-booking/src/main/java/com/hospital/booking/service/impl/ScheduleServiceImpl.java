package com.hospital.booking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.booking.entity.Schedule;
import com.hospital.booking.mapper.ScheduleMapper;
import com.hospital.booking.service.ScheduleService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * 排班服务实现 — 查询可用号源
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    @Override
    public List<Schedule> listAvailable(Long hospitalId, Long departmentId, LocalDate date) {
        return list(new LambdaQueryWrapper<Schedule>()
                .eq(hospitalId != null, Schedule::getHospitalId, hospitalId)
                .eq(departmentId != null, Schedule::getDepartmentId, departmentId)
                .eq(date != null, Schedule::getWorkDate, date)
                .gt(Schedule::getRemainQuota, 0)
                .orderByAsc(Schedule::getStartTime));
    }
}
