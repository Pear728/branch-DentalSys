-- 修复t_medical_record表中的visit_date字段问题

-- 1. 添加默认值到visit_date字段
ALTER TABLE `t_medical_record` 
  MODIFY COLUMN `visit_date` datetime DEFAULT CURRENT_TIMESTAMP NOT NULL;

-- 2. 将record_date的值复制到visit_date
UPDATE `t_medical_record` SET `visit_date` = `record_date` WHERE `visit_date` IS NULL;

-- 3. 如果需要，可以考虑完全删除visit_date字段（取消注释下面的语句）
-- ALTER TABLE `t_medical_record` DROP COLUMN `visit_date`;
