-- ==========================================
-- hospital_notify 数据库初始化脚本
-- ==========================================
CREATE TABLE IF NOT EXISTS `sms_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `content` VARCHAR(500) NOT NULL COMMENT '短信内容',
    `status` VARCHAR(20) DEFAULT 'SENT' COMMENT 'SENT/FAILED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信记录表';
