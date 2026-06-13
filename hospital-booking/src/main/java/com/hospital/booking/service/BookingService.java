package com.hospital.booking.service;

import com.hospital.common.R;

/**
 * 预约服务接口 — 提供挂号预约与取消功能
 */
public interface BookingService {
    /**
     * 创建预约（挂号），使用 Redis 分布式锁防止超卖
     */
    R<Long> createAppointment(Long userId, Long scheduleId);

    /**
     * 取消预约，自动释放号源
     */
    void cancelAppointment(Long appointmentId);
}
