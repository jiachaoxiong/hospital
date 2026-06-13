package com.hospital.notify.service;

/**
 * 短信服务接口 — 定义短信发送能力
 */
public interface SmsService {
    /**
     * 发送短信
     * @param phone   接收手机号
     * @param content 短信内容
     */
    void sendSms(String phone, String content);
}
