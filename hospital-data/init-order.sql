-- ==========================================
-- hospital_order 数据库初始化脚本
-- ==========================================
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `appointment_id` BIGINT NOT NULL COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '患者ID',
    `amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '金额',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/PAID/VISITED/CANCELLED',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
