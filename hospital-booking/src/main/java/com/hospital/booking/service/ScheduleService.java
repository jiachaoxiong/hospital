package com.hospital.booking.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.booking.entity.Schedule;
import java.time.LocalDate;
import java.util.List;

/**
 * 排班服务接口 — 提供排班查询与管理功能
 */
public interface ScheduleService extends IService<Schedule> {
    /** 查询可用排班（患者端，按医院、科室、日期过滤） */
    List<Schedule> listAvailable(Long hospitalId, Long departmentId, LocalDate date);

    /** 分页查询排班（管理员端） */
    Page<Schedule> pageQuery(Integer current, Integer size, Long hospitalId, Long departmentId);

    /** 新增排班 */
    void addSchedule(Schedule schedule);

    /** 更新排班 */
    void updateSchedule(Schedule schedule);

    /** 删除排班 */
    void removeSchedule(Long id);

    /** 查询某医生的所有排班 */
    List<Schedule> listByDoctorId(Long doctorId);

    /** 通过userId找到对应的doctorId（调用hospital服务） */
    Long getDoctorIdByUserId(Long userId);
}
