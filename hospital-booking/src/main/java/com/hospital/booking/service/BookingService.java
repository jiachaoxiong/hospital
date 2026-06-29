package com.hospital.booking.service;

import com.hospital.booking.entity.DoctorPatientVO;
import com.hospital.common.R;

import java.util.List;

/**
 * 预约服务接口 — 提供挂号预约与取消功能
 */
public interface BookingService {
    /**
     * 创建预约（挂号），使用 Redis 分布式锁防止超卖
     */
    R<Long> createAppointment(Long userId, Long scheduleId);

    /**
     * 取消预约，自动释放号源。需要校验归属（userId 必须匹配预约创建者）
     */
    void cancelAppointment(Long userId, Long appointmentId);

    /**
     * 医生查询预约自己的患者列表（通过userId找到doctorId，聚合患者、排班信息）
     */
    List<DoctorPatientVO> getDoctorPatients(Long userId);

    /**
     * 通过userId找到对应的doctorId
     */
    Long getDoctorIdByUserId(Long userId);

    /**
     * 支付成功后更新预约状态为 PAID（内部调用）
     */
    void markAsPaid(Long appointmentId);

    /**
     * 取消预约（内部调用，不受归属校验限制）
     */
    void cancelAppointmentInternal(Long appointmentId);
}
