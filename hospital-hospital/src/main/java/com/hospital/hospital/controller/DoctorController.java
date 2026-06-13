package com.hospital.hospital.controller;

import com.hospital.common.R;
import com.hospital.hospital.entity.Doctor;
import com.hospital.hospital.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生控制器 — 提供医生信息的查询 REST API
 */
@Tag(name = "医生接口", description = "医生信息的查询")
@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "根据科室ID查询医生列表")
    @GetMapping("/list")
    public R<List<Doctor>> list(@RequestParam Long departmentId) {
        return R.ok(doctorService.listByDepartmentId(departmentId));
    }
}
