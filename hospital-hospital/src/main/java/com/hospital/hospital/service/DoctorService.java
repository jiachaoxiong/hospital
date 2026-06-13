package com.hospital.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.hospital.entity.Doctor;

import java.util.List;

/**
 * 医生服务接口 — 提供医生的查询功能
 */
public interface DoctorService extends IService<Doctor> {

    /**
     * 根据科室ID查询该科室下的所有医生
     */
    List<Doctor> listByDepartmentId(Long departmentId);
}
