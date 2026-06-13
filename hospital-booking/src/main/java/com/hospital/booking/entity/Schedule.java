package com.hospital.booking.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班实体 — 记录医生在某天某时段的出诊安排和号源配额
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("schedule")
public class Schedule extends BaseEntity {
    /** 医生ID */
    private Long doctorId;
    /** 科室ID */
    private Long departmentId;
    /** 医院ID */
    private Long hospitalId;
    /** 出诊日期 */
    private LocalDate workDate;
    /** 开始时间 */
    private LocalTime startTime;
    /** 结束时间 */
    private LocalTime endTime;
    /** 总号源数 */
    private Integer totalQuota;
    /** 剩余号源数 */
    private Integer remainQuota;
    /** 挂号价格 */
    private Double price;
}
