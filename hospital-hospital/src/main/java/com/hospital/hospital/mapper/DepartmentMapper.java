package com.hospital.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.hospital.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科室数据访问层
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
