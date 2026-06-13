-- ==========================================
-- hospital_auth 数据库初始化脚本
-- ==========================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `phone` VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    `name` VARCHAR(50) DEFAULT '' COMMENT '姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'PATIENT' COMMENT '角色：PATIENT/DOCTOR/ADMIN',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始管理员用户 (密码: admin123)
INSERT INTO `user` (`phone`, `password`, `name`, `role`) VALUES
('13800000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', 'ADMIN');
