package com.hospital.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.R;
import com.hospital.hospital.entity.Hospital;
import com.hospital.hospital.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 医院控制器 — 提供医院信息的查询与管理 REST API
 */
@Tag(name = "医院接口", description = "医院信息的查询与管理")
@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(summary = "分页查询医院列表")
    @GetMapping("/list")
    public R<Page<Hospital>> list(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String city,
                                   @RequestParam(required = false) String level) {
        return R.ok(hospitalService.pageQuery(current, size, city, level));
    }

    @Operation(summary = "获取医院详情")
    @GetMapping("/{id}")
    public R<Hospital> detail(@PathVariable Long id) {
        return R.ok(hospitalService.getById(id));
    }
}
