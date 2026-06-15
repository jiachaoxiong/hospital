package com.hospital.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.R;
import com.hospital.hospital.entity.Doctor;
import com.hospital.hospital.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生控制器 — 提供医生信息的查询与管理的 REST API
 */
@Tag(name = "医生接口", description = "医生信息的查询与管理")
@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "根据科室ID查询医生列表（患者端用）")
    @GetMapping("/list")
    public R<List<Doctor>> list(@RequestParam(required = false) Long departmentId,
                                 @RequestParam(required = false) Long hospitalId) {
        return R.ok(doctorService.listByCondition(departmentId, hospitalId));
    }

    @Operation(summary = "分页查询医生列表（管理员端用）")
    @GetMapping("/page")
    public R<Page<Doctor>> page(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestParam(required = false) Long hospitalId,
                                 @RequestParam(required = false) Long departmentId) {
        return R.ok(doctorService.pageQuery(current, size, hospitalId, departmentId));
    }

    @Operation(summary = "新增医生")
    @PostMapping("/add")
    public R<Void> add(@RequestBody Doctor doctor) {
        doctorService.addDoctor(doctor);
        return R.ok();
    }

    @Operation(summary = "更新医生")
    @PostMapping("/update")
    public R<Void> update(@RequestBody Doctor doctor) {
        doctorService.updateById(doctor);
        return R.ok();
    }

    @Operation(summary = "删除医生")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        doctorService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "根据auth用户ID查找医生")
    @GetMapping("/byUserId/{userId}")
    public R<Doctor> findByUserId(@PathVariable Long userId) {
        Doctor doctor = doctorService.findByUserId(userId);
        if (doctor == null) {
            return R.fail("医生信息不存在，请先关联医生档案");
        }
        return R.ok(doctor);
    }
}
