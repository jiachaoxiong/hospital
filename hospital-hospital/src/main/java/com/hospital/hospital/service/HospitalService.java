package com.hospital.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.hospital.entity.Hospital;

/**
 * 医院服务接口 — 提供医院的分页查询和详情获取
 */
public interface HospitalService extends IService<Hospital> {

    /**
     * 分页查询医院列表，支持按城市和等级筛选
     */
    Page<Hospital> pageQuery(Integer current, Integer size, String city, String level);
}
