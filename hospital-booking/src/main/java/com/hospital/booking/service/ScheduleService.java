package com.hospital.booking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.booking.entity.Schedule;
import java.time.LocalDate;
import java.util.List;

/**
 * 排班服务接口 — 提供排班查询功能
 */
public interface ScheduleService extends IService<Schedule> {
    /**
     * 查询可用排班（按医院、科室、日期过滤，仅返回有剩余号源的排班）
     */
    List<Schedule> listAvailable(Long hospitalId, Long departmentId, LocalDate date);
}
