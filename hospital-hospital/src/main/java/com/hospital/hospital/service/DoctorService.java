package com.hospital.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.hospital.entity.Doctor;

import java.util.List;

/**
 * 医生服务接口 — 提供医生的查询与管理功能
 */
public interface DoctorService extends IService<Doctor> {

    /** 按条件查询医生列表（患者端用） */
    List<Doctor> listByCondition(Long departmentId, Long hospitalId);

    /** 分页查询医生列表（管理员端用） */
    Page<Doctor> pageQuery(Integer current, Integer size, Long hospitalId, Long departmentId);

    /** 新增医生 */
    void addDoctor(Doctor doctor);

    /** 根据auth用户ID查找医生 */
    Doctor findByUserId(Long userId);
}
