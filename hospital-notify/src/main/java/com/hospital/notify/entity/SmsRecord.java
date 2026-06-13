package com.hospital.notify.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短信发送记录实体 — 记录每次短信发送的手机号、内容及状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sms_record")
public class SmsRecord extends BaseEntity {
    /** 接收短信的手机号 */
    private String phone;
    /** 短信内容 */
    private String content;
    /** 发送状态：SENT=成功 FAILED=失败 */
    private String status;
}
