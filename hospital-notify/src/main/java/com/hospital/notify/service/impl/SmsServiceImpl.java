package com.hospital.notify.service.impl;

import com.hospital.notify.entity.SmsRecord;
import com.hospital.notify.mapper.SmsRecordMapper;
import com.hospital.notify.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现 — 模拟短信发送，记录到数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsRecordMapper smsRecordMapper;

    @Override
    public void sendSms(String phone, String content) {
        log.info("【模拟短信】发送到 {}: {}", phone, content);
        SmsRecord record = new SmsRecord();
        record.setPhone(phone);
        record.setContent(content);
        record.setStatus("SENT");
        smsRecordMapper.insert(record);
    }
}
