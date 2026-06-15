package com.hospital.booking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.booking.entity.Schedule;
import com.hospital.booking.service.ScheduleService;
import com.hospital.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 排班接口 — 患者端查可用号源 + 管理员端CRUD管理
 */
@Tag(name = "排班接口")
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // === 患者端 ===

    @Operation(summary = "患者查询可用排班")
    @GetMapping("/available")
    public R<List<Schedule>> available(@RequestParam Long hospitalId,
                                        @RequestParam Long departmentId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return R.ok(scheduleService.listAvailable(hospitalId, departmentId, date));
    }

    // === 管理员端 ===

    @Operation(summary = "管理员分页查询排班")
    @GetMapping("/list")
    public R<Page<Schedule>> list(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) Long hospitalId,
                                   @RequestParam(required = false) Long departmentId) {
        return R.ok(scheduleService.pageQuery(current, size, hospitalId, departmentId));
    }

    @Operation(summary = "新增排班")
    @PostMapping("/add")
    public R<Void> add(@RequestBody Schedule schedule) {
        scheduleService.addSchedule(schedule);
        return R.ok();
    }

    @Operation(summary = "更新排班")
    @PostMapping("/update")
    public R<Void> update(@RequestBody Schedule schedule) {
        scheduleService.updateSchedule(schedule);
        return R.ok();
    }

    @Operation(summary = "删除排班")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        scheduleService.removeSchedule(id);
        return R.ok();
    }

    // === 医生端 ===

    @Operation(summary = "医生查询自己的排班")
    @GetMapping("/doctor/my")
    public R<List<Schedule>> doctorMy(@RequestHeader("X-User-Id") Long userId) {
        // 通过 userId 找到对应的 doctorId，再查询排班
        Long doctorId = scheduleService.getDoctorIdByUserId(userId);
        return R.ok(scheduleService.listByDoctorId(doctorId));
    }
}
