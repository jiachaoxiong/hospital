-- ==========================================
-- hospital_payment 数据库初始化脚本
-- ==========================================
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `pay_method` VARCHAR(20) DEFAULT 'WECHAT' COMMENT '支付方式',
    `trade_no` VARCHAR(64) NOT NULL COMMENT '交易流水号',
    `amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '金额',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/SUCCESS/REFUNDED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';
