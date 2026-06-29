package com.hospital.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体 — 存储患者、医生、管理员三种角色的用户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    private String phone;      // 手机号
    @JsonIgnore                // 防止密码哈希通过JSON序列化泄露
    private String password;   // BCrypt加密密码
    private String name;       // 真实姓名
    private String role;       // PATIENT / DOCTOR / ADMIN
    private Integer status;    // 1=启用 0=禁用
}
