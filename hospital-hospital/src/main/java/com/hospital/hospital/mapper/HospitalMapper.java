package com.hospital.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.hospital.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医院数据访问层
 */
@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {
}
