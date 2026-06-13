package com.hospital.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医院实体 — 存储医院基本信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hospital")
public class Hospital extends BaseEntity {

    /** 医院名称 */
    private String name;

    /** 医院等级：三级甲等、三级乙等等 */
    private String level;

    /** 所在省份 */
    private String province;

    /** 所在城市 */
    private String city;

    /** 详细地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 医院简介 */
    private String description;

    /** 医院图片URL */
    private String imageUrl;
}
