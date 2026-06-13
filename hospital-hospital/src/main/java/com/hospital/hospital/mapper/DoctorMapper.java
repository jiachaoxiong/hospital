package com.hospital.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.hospital.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生数据访问层
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
}
