package com.hospital.hospital.controller;

import com.hospital.common.R;
import com.hospital.hospital.entity.Department;
import com.hospital.hospital.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室控制器 — 提供科室信息的查询 REST API
 */
@Tag(name = "科室接口", description = "科室信息的查询")
@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "根据医院ID查询科室列表")
    @GetMapping("/list")
    public R<List<Department>> list(@RequestParam Long hospitalId) {
        return R.ok(departmentService.listByHospitalId(hospitalId));
    }
}
