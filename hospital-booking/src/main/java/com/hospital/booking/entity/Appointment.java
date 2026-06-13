package com.hospital.booking.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预约实体 — 记录用户的挂号预约信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment")
public class Appointment extends BaseEntity {
    /** 用户ID */
    private Long userId;
    /** 排班ID */
    private Long scheduleId;
    /** 医院ID */
    private Long hospitalId;
    /** 科室ID */
    private Long departmentId;
    /** 医生ID */
    private Long doctorId;
    /** 状态：PENDING=待支付 / PAID=已支付 / VISITED=已就诊 / CANCELLED=已取消 */
    private String status;
}
