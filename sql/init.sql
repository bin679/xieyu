-- ============================================
-- 高校实验室设备管理系统 - 数据库初始化脚本
-- Database: lab_manager
-- ============================================

CREATE DATABASE IF NOT EXISTS lab_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lab_manager;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS `repair_record`;
DROP TABLE IF EXISTS `borrow_record`;
DROP TABLE IF EXISTS `equipment`;
DROP TABLE IF EXISTS `lab`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色: admin/user',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 实验室表
-- ============================================
CREATE TABLE `lab` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '实验室ID',
    `name` VARCHAR(100) NOT NULL COMMENT '实验室名称',
    `location` VARCHAR(200) NOT NULL COMMENT '实验室地点',
    `capacity` INT NOT NULL DEFAULT 0 COMMENT '容纳人数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '状态: available/maintenance/closed',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室表';

-- ============================================
-- 3. 设备表
-- ============================================
CREATE TABLE `equipment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    `name` VARCHAR(100) NOT NULL COMMENT '设备名称',
    `model` VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
    `lab_id` BIGINT NOT NULL COMMENT '所属实验室ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '状态: available/borrowed/maintenance/scrapped',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_lab_id` (`lab_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_equipment_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- ============================================
-- 4. 设备借用记录表
-- ============================================
CREATE TABLE `borrow_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `equipment_id` BIGINT NOT NULL COMMENT '设备ID',
    `user_id` BIGINT NOT NULL COMMENT '借用人ID',
    `borrow_time` DATETIME NOT NULL COMMENT '借用时间',
    `planned_return_time` DATETIME NOT NULL COMMENT '计划归还时间',
    `actual_return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'borrowing' COMMENT '状态: borrowing/returned/overdue',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_borrow_time` (`borrow_time`),
    CONSTRAINT `fk_borrow_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备借用记录表';

-- ============================================
-- 5. 维修记录表
-- ============================================
CREATE TABLE `repair_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `equipment_id` BIGINT NOT NULL COMMENT '设备ID',
    `user_id` BIGINT NOT NULL COMMENT '报修人ID',
    `description` VARCHAR(500) NOT NULL COMMENT '故障描述',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/repairing/completed',
    `repair_time` DATETIME DEFAULT NULL COMMENT '维修开始时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '维修完成时间',
    `result` VARCHAR(500) DEFAULT NULL COMMENT '维修结果',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_repair_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_repair_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修记录表';

-- ============================================
-- 初始数据 (实验室和设备)
-- ============================================
-- 用户请通过系统注册页面创建，密码将使用 BCrypt 加密存储
-- 创建用户后，如需管理员权限，在数据库中执行:
--   UPDATE user SET role = 'admin' WHERE username = '你的用户名';

INSERT INTO `lab` (`name`, `location`, `capacity`, `status`, `description`) VALUES
('计算机网络实验室', '教学楼A-301', 60, 'available', '配备60台计算机，用于网络课程实验'),
('软件工程实验室', '教学楼B-102', 45, 'available', '配备开发工作站，用于软件工程实践'),
('嵌入式系统实验室', '实验楼C-201', 30, 'available', '配备嵌入式开发板及调试设备'),
('人工智能实验室', '实验楼C-401', 40, 'maintenance', '配备GPU服务器，用于AI课程实验（维护中）');

INSERT INTO `equipment` (`name`, `model`, `lab_id`, `status`, `description`) VALUES
('台式计算机', 'Dell OptiPlex 7090', 1, 'available', 'i7-11700/16GB/512GB SSD'),
('网络交换机', 'Huawei S5735-L24T4X-A', 1, 'available', '24口千兆交换机'),
('开发工作站', 'HP Z4 G4', 2, 'available', 'Xeon W-2223/32GB/1TB SSD'),
('树莓派开发板', 'Raspberry Pi 4 Model B', 3, 'available', '4GB RAM版本'),
('GPU服务器', 'Dell PowerEdge T640', 4, 'maintenance', '4x RTX 3090/256GB RAM'),
('示波器', 'Tektronix TDS2024C', 3, 'available', '200MHz 4通道'),
('3D打印机', 'Ultimaker S5', 3, 'available', '双喷头打印'),
('投影仪', 'Epson CB-X51', 2, 'available', 'XGA 3800流明');
