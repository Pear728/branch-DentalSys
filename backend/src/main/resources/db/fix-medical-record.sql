-- 修复t_medical_record表中的重复字段问题
-- 删除驼峰命名的重复字段

-- 首先备份表结构
CREATE TABLE IF NOT EXISTS `t_medical_record_backup` LIKE `t_medical_record`;
INSERT INTO `t_medical_record_backup` SELECT * FROM `t_medical_record`;

-- 修改表结构，删除重复字段
ALTER TABLE `t_medical_record` 
  DROP COLUMN IF EXISTS `appointmentId`,
  DROP COLUMN IF EXISTS `chiefComplaint`,
  DROP COLUMN IF EXISTS `createTime`,
  DROP COLUMN IF EXISTS `doctorId`,
  DROP COLUMN IF EXISTS `examinationAdvice`,
  DROP COLUMN IF EXISTS `followUpPlan`,
  DROP COLUMN IF EXISTS `patientId`,
  DROP COLUMN IF EXISTS `treatmentPlan`,
  DROP COLUMN IF EXISTS `updateTime`,
  DROP COLUMN IF EXISTS `visitDate`,
  DROP COLUMN IF EXISTS `examination_advice`,
  DROP COLUMN IF EXISTS `precautions`,
  DROP COLUMN IF EXISTS `treatment_plan`,
  DROP COLUMN IF EXISTS `visit_date`;

-- 确保所有必要字段都存在
ALTER TABLE `t_medical_record`
  MODIFY COLUMN `record_date` date NOT NULL COMMENT '就诊日期',
  MODIFY COLUMN `record_no` varchar(30) NOT NULL COMMENT '病历编号',
  MODIFY COLUMN `patient_id` bigint NOT NULL COMMENT '患者ID（关联t_user表）',
  MODIFY COLUMN `doctor_id` bigint NOT NULL COMMENT '医生ID（关联t_user表）';

-- 添加默认值
ALTER TABLE `t_medical_record`
  MODIFY COLUMN `status` tinyint(1) DEFAULT 1 COMMENT '状态（1-有效，0-无效）';
