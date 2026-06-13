-- ==========================================
-- hospital_booking 数据库初始化脚本
-- ==========================================
CREATE TABLE IF NOT EXISTS `schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `department_id` BIGINT NOT NULL COMMENT '科室ID',
    `hospital_id` BIGINT NOT NULL COMMENT '医院ID',
    `work_date` DATE NOT NULL COMMENT '出诊日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `total_quota` INT DEFAULT 20 COMMENT '号源总数',
    `remain_quota` INT DEFAULT 20 COMMENT '剩余号源',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '挂号费',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班表';

CREATE TABLE IF NOT EXISTS `appointment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '患者ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排班ID',
    `hospital_id` BIGINT NOT NULL COMMENT '医院ID',
    `department_id` BIGINT NOT NULL COMMENT '科室ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/PAID/VISITED/CANCELLED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';
