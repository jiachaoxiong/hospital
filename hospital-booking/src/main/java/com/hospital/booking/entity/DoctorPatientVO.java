package com.hospital.booking.entity;

import lombok.Data;

/**
 * 医生端患者列表VO — 聚合预约、排班、用户信息
 */
@Data
public class DoctorPatientVO {
    /** 预约ID */
    private Long appointmentId;
    /** 患者姓名（来自auth服务） */
    private String patientName;
    /** 患者手机号（来自auth服务） */
    private String patientPhone;
    /** 医院名称（来自schedule） */
    private String hospitalName;
    /** 科室名称（来自schedule） */
    private String departmentName;
    /** 就诊日期 */
    private String workDate;
    /** 就诊时段 */
    private String timeSlot;
    /** 预约状态 */
    private String status;
    /** 医生姓名 */
    private String doctorName;
}
