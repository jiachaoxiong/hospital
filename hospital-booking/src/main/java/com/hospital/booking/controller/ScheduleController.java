package com.hospital.booking.controller;

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
 * 排班接口 — 查询医生出诊排班和剩余号源
 */
@Tag(name = "排班接口")
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(summary = "查询可用排班")
    @GetMapping("/available")
    public R<List<Schedule>> available(@RequestParam Long hospitalId,
                                        @RequestParam Long departmentId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return R.ok(scheduleService.listAvailable(hospitalId, departmentId, date));
    }
}
