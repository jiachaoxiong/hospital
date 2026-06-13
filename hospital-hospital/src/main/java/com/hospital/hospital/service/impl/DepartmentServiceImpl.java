package com.hospital.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.hospital.entity.Department;
import com.hospital.hospital.mapper.DepartmentMapper;
import com.hospital.hospital.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 科室服务实现 — 科室查询的核心业务逻辑
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<Department> listByHospitalId(Long hospitalId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getHospitalId, hospitalId);
        wrapper.orderByAsc(Department::getId);
        return list(wrapper);
    }
}
