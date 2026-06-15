package com.hospital.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.hospital.entity.Doctor;
import com.hospital.hospital.mapper.DoctorMapper;
import com.hospital.hospital.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 医生服务实现 — 医生查询与管理的核心业务逻辑
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Override
    public List<Doctor> listByCondition(Long departmentId, Long hospitalId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (departmentId != null) wrapper.eq(Doctor::getDepartmentId, departmentId);
        if (hospitalId != null) wrapper.eq(Doctor::getHospitalId, hospitalId);
        wrapper.orderByAsc(Doctor::getId);
        return list(wrapper);
    }

    @Override
    public Page<Doctor> pageQuery(Integer current, Integer size, Long hospitalId, Long departmentId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (hospitalId != null) wrapper.eq(Doctor::getHospitalId, hospitalId);
        if (departmentId != null) wrapper.eq(Doctor::getDepartmentId, departmentId);
        wrapper.orderByAsc(Doctor::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public void addDoctor(Doctor doctor) {
        save(doctor);
    }

    @Override
    public Doctor findByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<Doctor>().eq(Doctor::getUserId, userId));
    }
}
