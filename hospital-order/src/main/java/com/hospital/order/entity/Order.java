package com.hospital.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 订单实体 — 记录用户预约挂号产生的支付订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`order`")
public class Order extends BaseEntity {
    /** 关联的预约记录ID */
    private Long appointmentId;
    /** 下单用户ID */
    private Long userId;
    /** 订单金额 */
    private Double amount;
    /** 订单状态：PENDING=待支付 / PAID=已支付 / VISITED=已就诊 / CANCELLED=已取消 */
    private String status;
    /** 支付完成时间 */
    private LocalDateTime payTime;
}
