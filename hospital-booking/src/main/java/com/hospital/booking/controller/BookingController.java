package com.hospital.booking.controller;

import com.hospital.booking.service.BookingService;
import com.hospital.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 预约接口 — 挂号预约与取消
 */
@Tag(name = "预约接口")
@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "创建预约（挂号）")
    @PostMapping("/create")
    public R<Long> create(@RequestHeader("X-User-Id") Long userId,
                          @RequestParam Long scheduleId) {
        return bookingService.createAppointment(userId, scheduleId);
    }

    @Operation(summary = "取消预约")
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        bookingService.cancelAppointment(id);
        return R.ok();
    }
}
