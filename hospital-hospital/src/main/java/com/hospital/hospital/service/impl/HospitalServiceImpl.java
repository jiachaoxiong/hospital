package com.hospital.hospital.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.hospital.entity.Hospital;
import com.hospital.hospital.mapper.HospitalMapper;
import com.hospital.hospital.service.HospitalService;
import org.springframework.stereotype.Service;

/**
 * 医院服务实现 — 医院分页查询的核心业务逻辑
 */
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements HospitalService {

    @Override
    public Page<Hospital> pageQuery(Integer current, Integer size, String city, String level) {
        LambdaQueryWrapper<Hospital> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(city), Hospital::getCity, city);
        wrapper.eq(StrUtil.isNotBlank(level), Hospital::getLevel, level);
        wrapper.orderByAsc(Hospital::getId);
        return page(new Page<>(current, size), wrapper);
    }
}
