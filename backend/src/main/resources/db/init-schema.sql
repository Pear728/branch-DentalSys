-- 创建数据库
CREATE DATABASE IF NOT EXISTS dental_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE dental_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名/账号',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint(1) DEFAULT '1' COMMENT '性别(1-男，0-女)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) NOT NULL COMMENT '角色(PATIENT-患者,DOCTOR-医生,ADMIN-管理员)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(1-正常,0-禁用)',
  `salt` varchar(50) DEFAULT NULL COMMENT '密码盐值',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 患者信息表（扩展患者角色的用户信息）
CREATE TABLE IF NOT EXISTS `t_patient_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `address` varchar(255) DEFAULT NULL COMMENT '住址',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系人电话',
  `medical_history` text DEFAULT NULL COMMENT '病史',
  `allergies` text DEFAULT NULL COMMENT '过敏史',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_patient_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';

-- 医生信息表（扩展医生角色的用户信息）
CREATE TABLE IF NOT EXISTS `t_doctor_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `department` varchar(50) DEFAULT NULL COMMENT '科室',
  `professional_title` varchar(50) DEFAULT NULL COMMENT '职称',
  `specialty` varchar(255) DEFAULT NULL COMMENT '专长',
  `introduction` text DEFAULT NULL COMMENT '简介',
  `certificate_no` varchar(50) DEFAULT NULL COMMENT '执业证号',
  `years_of_experience` int(11) DEFAULT '0' COMMENT '从业年限',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`),
  KEY `idx_department` (`department`),
  CONSTRAINT `fk_doctor_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生信息表';

-- 排班表
CREATE TABLE IF NOT EXISTS `t_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生ID（关联t_user表）',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `start_time` varchar(10) NOT NULL COMMENT '开始时间（格式HH:MM）',
  `end_time` varchar(10) NOT NULL COMMENT '结束时间（格式HH:MM）',
  `max_appointments` int(11) DEFAULT '10' COMMENT '最大预约数',
  `appointed_count` int(11) DEFAULT '0' COMMENT '已预约数量',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1-可预约，0-不可预约）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_doctor_date` (`doctor_id`,`schedule_date`),
  KEY `idx_date` (`schedule_date`),
  CONSTRAINT `fk_schedule_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班表';

-- 预约表
CREATE TABLE IF NOT EXISTS `t_appointment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `appointment_no` varchar(30) NOT NULL COMMENT '预约编号',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID（关联t_user表）',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生ID（关联t_user表）',
  `schedule_id` bigint(20) NOT NULL COMMENT '排班ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `appointment_time` varchar(20) NOT NULL COMMENT '预约时间段',
  `symptoms` text DEFAULT NULL COMMENT '症状描述',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态（0-待确认，1-已确认，2-已完成，3-已取消）',
  `order_amount` decimal(10,2) DEFAULT '0.00' COMMENT '预约金额',
  `payment_status` tinyint(1) DEFAULT '0' COMMENT '支付状态（0-未支付，1-已支付）',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '交易流水号',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_appointment_no` (`appointment_no`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_appointment_patient` FOREIGN KEY (`patient_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `fk_appointment_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `fk_appointment_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `t_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 病历表
CREATE TABLE IF NOT EXISTS `t_medical_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '病历ID',
  `record_no` varchar(30) NOT NULL COMMENT '病历编号',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID（关联t_user表）',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生ID（关联t_user表）',
  `appointment_id` bigint(20) DEFAULT NULL COMMENT '关联的预约ID',
  `record_date` date NOT NULL COMMENT '就诊日期',
  `diagnosis` text DEFAULT NULL COMMENT '诊断结果',
  `treatment` text DEFAULT NULL COMMENT '治疗方案',
  `prescription` text DEFAULT NULL COMMENT '处方',
  `next_visit_suggestion` text DEFAULT NULL COMMENT '下次就诊建议',
  `teeth_condition` text DEFAULT NULL COMMENT '牙齿状况记录',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1-有效，0-无效）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_record_no` (`record_no`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_record_date` (`record_date`),
  CONSTRAINT `fk_record_patient` FOREIGN KEY (`patient_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `fk_record_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `fk_record_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `t_appointment` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS `t_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `payment_no` varchar(30) NOT NULL COMMENT '支付编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `appointment_id` bigint(20) DEFAULT NULL COMMENT '关联的预约ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '第三方交易流水号',
  `payment_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '支付状态（0-未支付，1-已支付，2-已退款）',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `refund_status` tinyint(1) DEFAULT '0' COMMENT '退款状态（0-未退款，1-已退款）',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额',
  `payment_type` varchar(20) DEFAULT NULL COMMENT '支付类型（预约挂号费、治疗费等）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_payment_no` (`payment_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_payment_status` (`payment_status`),
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `fk_payment_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `t_appointment` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 系统设置表
CREATE TABLE IF NOT EXISTS `t_system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_desc` varchar(100) DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 系统操作日志表
CREATE TABLE IF NOT EXISTS `t_system_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '操作',
  `method` varchar(100) DEFAULT NULL COMMENT '请求方法',
  `params` text DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `status` tinyint(1) DEFAULT '1' COMMENT '操作状态（1-成功，0-失败）',
  `error_msg` text DEFAULT NULL COMMENT '错误消息',
  `operation_time` bigint(20) DEFAULT NULL COMMENT '操作耗时(毫秒)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';
