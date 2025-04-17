-- 修复t_schedule表中的重复字段问题

-- 1. 将appointedCount字段的值复制到appointed_count字段
UPDATE t_schedule SET appointed_count = appointedCount WHERE appointed_count IS NULL;

-- 2. 删除重复的驼峰命名字段
ALTER TABLE t_schedule
  DROP COLUMN appointedCount,
  DROP COLUMN createTime,
  DROP COLUMN doctorId,
  DROP COLUMN endTime,
  DROP COLUMN maxAppointments,
  DROP COLUMN startTime,
  DROP COLUMN updateTime;

-- 3. 确保appointed_count字段不为NULL且有默认值
ALTER TABLE t_schedule MODIFY COLUMN appointed_count INT(11) NOT NULL DEFAULT 0 COMMENT '已预约数量';

-- 4. 添加一条注释说明已修复问题
INSERT INTO t_system_config (config_key, config_value, config_desc) 
VALUES ('schedule_table_fixed', 'true', '已修复t_schedule表中的重复字段问题') 
ON DUPLICATE KEY UPDATE config_value = 'true', config_desc = '已修复t_schedule表中的重复字段问题';
