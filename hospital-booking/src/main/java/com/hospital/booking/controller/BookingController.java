package com.hospital.booking.controller;

import com.hospital.booking.entity.DoctorPatientVO;
import com.hospital.booking.service.BookingService;
import com.hospital.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public R<Void> cancel(@RequestHeader("X-User-Id") Long userId,
                          @PathVariable Long id) {
        bookingService.cancelAppointment(userId, id);
        return R.ok();
    }

    @Operation(summary = "医生查询自己的患者列表")
    @GetMapping("/doctor/patients")
    public R<List<DoctorPatientVO>> doctorPatients(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(bookingService.getDoctorPatients(userId));
    }

    @Operation(summary = "内部接口：支付成功后更新预约状态为PAID")
    @PostMapping("/paid/{appointmentId}")
    public R<Void> markPaid(@PathVariable Long appointmentId) {
        bookingService.markAsPaid(appointmentId);
        return R.ok();
    }

    @Operation(summary = "内部接口：取消预约（不受IDOR限制，供其他服务调用）")
    @PostMapping("/internal/cancel/{appointmentId}")
    public R<Void> internalCancel(@PathVariable Long appointmentId) {
        bookingService.cancelAppointmentInternal(appointmentId);
        return R.ok();
    }
}
