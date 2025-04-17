-- 全面修复t_medical_record表中的字段问题

-- 首先备份表结构
CREATE TABLE IF NOT EXISTS `t_medical_record_backup_full` LIKE `t_medical_record`;
INSERT INTO `t_medical_record_backup_full` SELECT * FROM `t_medical_record`;

-- 删除所有驼峰命名的重复字段
-- 首先检查列是否存在，然后删除

-- 检查并删除appointmentId列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'appointmentId');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `appointmentId`', 'SELECT "Column appointmentId does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除chiefComplaint列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'chiefComplaint');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `chiefComplaint`', 'SELECT "Column chiefComplaint does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除createTime列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'createTime');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `createTime`', 'SELECT "Column createTime does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除doctorId列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'doctorId');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `doctorId`', 'SELECT "Column doctorId does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除examinationAdvice列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'examinationAdvice');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `examinationAdvice`', 'SELECT "Column examinationAdvice does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除followUpPlan列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'followUpPlan');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `followUpPlan`', 'SELECT "Column followUpPlan does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除patientId列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'patientId');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `patientId`', 'SELECT "Column patientId does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除precautions列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'precautions');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `precautions`', 'SELECT "Column precautions does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除treatmentPlan列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'treatmentPlan');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `treatmentPlan`', 'SELECT "Column treatmentPlan does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除updateTime列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'updateTime');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `updateTime`', 'SELECT "Column updateTime does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除visitDate列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'visitDate');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `visitDate`', 'SELECT "Column visitDate does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除visit_date列
SET @exists := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_medical_record' AND COLUMN_NAME = 'visit_date');
SET @query = IF(@exists > 0, 'ALTER TABLE `t_medical_record` DROP COLUMN `visit_date`', 'SELECT "Column visit_date does not exist"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 确保所有必要字段都存在且有正确的默认值
ALTER TABLE `t_medical_record`
  MODIFY COLUMN `appointment_id` bigint NULL COMMENT '关联的预约ID',
  MODIFY COLUMN `patient_id` bigint NOT NULL COMMENT '患者ID',
  MODIFY COLUMN `doctor_id` bigint NOT NULL COMMENT '医生ID',
  MODIFY COLUMN `record_date` date NOT NULL COMMENT '就诊日期',
  MODIFY COLUMN `record_no` varchar(30) NOT NULL COMMENT '病历编号',
  MODIFY COLUMN `status` tinyint(1) DEFAULT 1 COMMENT '状态（1-有效，0-无效）',
  MODIFY COLUMN `chief_complaint` text NULL COMMENT '主诉',
  MODIFY COLUMN `diagnosis` text NULL COMMENT '诊断结果',
  MODIFY COLUMN `treatment` text NULL COMMENT '治疗方案',
  MODIFY COLUMN `prescription` text NULL COMMENT '处方',
  MODIFY COLUMN `next_visit_suggestion` text NULL COMMENT '下次就诊建议',
  MODIFY COLUMN `teeth_condition` text NULL COMMENT '牙齿状况记录',
  MODIFY COLUMN `remark` varchar(255) NULL COMMENT '备注';
