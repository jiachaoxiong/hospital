package com.hospital.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.hospital.entity.Doctor;
import com.hospital.hospital.mapper.DoctorMapper;
import com.hospital.hospital.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 医生服务实现 — 医生查询的核心业务逻辑
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Override
    public List<Doctor> listByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getDepartmentId, departmentId);
        wrapper.orderByAsc(Doctor::getId);
        return list(wrapper);
    }
}
