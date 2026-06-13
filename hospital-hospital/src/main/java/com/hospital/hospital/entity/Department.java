package com.hospital.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室实体 — 存储医院科室信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("department")
public class Department extends BaseEntity {

    /** 所属医院ID */
    private Long hospitalId;

    /** 科室名称 */
    private String name;

    /** 科室描述 */
    private String description;
}
