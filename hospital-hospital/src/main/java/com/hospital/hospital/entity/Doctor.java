package com.hospital.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医生实体 — 存储医生基本信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doctor")
public class Doctor extends BaseEntity {

    /** 所属科室ID */
    private Long departmentId;

    /** 所属医院ID */
    private Long hospitalId;

    /** 医生姓名 */
    private String name;

    /** 职称：主任医师、副主任医师等 */
    private String title;

    /** 医生简介/擅长领域 */
    private String description;

    /** 头像URL */
    private String avatarUrl;

    /** 关联auth服务的用户ID，用于医生登录后查找自己的数据 */
    private Long userId;
}
