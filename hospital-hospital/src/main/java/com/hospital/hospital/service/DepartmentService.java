package com.hospital.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.hospital.entity.Department;

import java.util.List;

/**
 * 科室服务接口 — 提供科室的查询功能
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 根据医院ID查询该医院下的所有科室
     */
    List<Department> listByHospitalId(Long hospitalId);
}
