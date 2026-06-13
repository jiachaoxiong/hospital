package com.hospital.payment.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_record")
public class PaymentRecord extends BaseEntity {
    private Long orderId;
    private String payMethod;   // WECHAT
    private String tradeNo;     // 模拟流水号
    private Double amount;
    private String status;      // PENDING / SUCCESS / REFUNDED
}
