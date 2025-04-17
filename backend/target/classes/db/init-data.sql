-- 使用数据库
USE dental_system;

-- 初始化管理员账户 (密码为md5加密后的 admin123)
INSERT INTO `t_user` (`username`, `password`, `real_name`, `gender`, `phone`, `email`, `role`, `status`, `salt`, `remark`, `create_time`)
VALUES ('admin', 'e5a3e61756fa9a2695a5d2aa99efba2b', '系统管理员', 1, '13800138000', 'admin@dental.com', 'ADMIN', 1, 'abcdef', '系统初始化创建', NOW());

-- 初始化医生账户 (密码为md5加密后的 doctor123)
INSERT INTO `t_user` (`username`, `password`, `real_name`, `gender`, `phone`, `email`, `role`, `status`, `salt`, `remark`, `create_time`)
VALUES 
('doctor1', 'df882c01129dfc0fe37e030ad87bd1e3', '张医生', 1, '13800138001', 'zhang@dental.com', 'DOCTOR', 1, 'ghijkl', '系统初始化创建', NOW()),
('doctor2', 'df882c01129dfc0fe37e030ad87bd1e3', '李医生', 1, '13800138002', 'li@dental.com', 'DOCTOR', 1, 'mnopqr', '系统初始化创建', NOW()),
('doctor3', 'df882c01129dfc0fe37e030ad87bd1e3', '王医生', 0, '13800138003', 'wang@dental.com', 'DOCTOR', 1, 'stuvwx', '系统初始化创建', NOW());

-- 初始化医生信息
INSERT INTO `t_doctor_info` (`user_id`, `department`, `professional_title`, `specialty`, `introduction`, `certificate_no`, `years_of_experience`) 
VALUES 
((SELECT id FROM t_user WHERE username = 'doctor1'), '口腔内科', '主任医师', '根管治疗、牙体牙髓病', '张医生从事口腔临床工作20余年，擅长各类牙体牙髓疾病的诊治，尤其在根管治疗方面有丰富的经验，多次参加国内外口腔医学学术会议并发表论文。', 'D2020001', 20),
((SELECT id FROM t_user WHERE username = 'doctor2'), '口腔外科', '副主任医师', '牙齿拔除、牙列矫正', '李医生毕业于北京大学口腔医学院，从事口腔外科临床工作15年，擅长各种复杂牙齿拔除和口腔颌面部小手术。', 'D2020002', 15),
((SELECT id FROM t_user WHERE username = 'doctor3'), '口腔修复科', '主治医师', '烤瓷牙、全瓷冠、义齿修复', '王医生专注于口腔修复领域10年，擅长各类固定修复和可摘义齿修复，尤其在前牙美学修复方面有独到见解。', 'D2020003', 10);

-- 初始化患者账户 (密码为md5加密后的 patient123)
INSERT INTO `t_user` (`username`, `password`, `real_name`, `gender`, `phone`, `email`, `role`, `status`, `salt`, `remark`, `create_time`)
VALUES 
('patient1', 'af11d8afb73a848756f12449f7b19618', '刘患者', 1, '13800138004', 'liu@example.com', 'PATIENT', 1, 'yzabcd', '系统初始化创建', NOW()),
('patient2', 'af11d8afb73a848756f12449f7b19618', '陈患者', 0, '13800138005', 'chen@example.com', 'PATIENT', 1, 'efghij', '系统初始化创建', NOW()),
('patient3', 'af11d8afb73a848756f12449f7b19618', '赵患者', 1, '13800138006', 'zhao@example.com', 'PATIENT', 1, 'klmnop', '系统初始化创建', NOW());

-- 初始化患者信息
INSERT INTO `t_patient_info` (`user_id`, `birth_date`, `id_card`, `address`, `emergency_contact`, `emergency_phone`, `medical_history`, `allergies`) 
VALUES 
((SELECT id FROM t_user WHERE username = 'patient1'), '1985-05-15', '110101198505150011', '北京市海淀区中关村南大街5号', '刘父', '13900139000', '曾做过智齿拔除手术', '青霉素过敏'),
((SELECT id FROM t_user WHERE username = 'patient2'), '1990-10-20', '110101199010200022', '北京市朝阳区建国路89号', '陈母', '13900139001', '无重大病史', '无'),
((SELECT id FROM t_user WHERE username = 'patient3'), '1978-03-08', '110101197803080033', '北京市西城区西直门外大街1号', '赵配偶', '13900139002', '高血压，定期服用降压药', '磺胺类药物过敏');

-- 初始化医生排班数据
INSERT INTO `t_schedule` (`doctor_id`, `schedule_date`, `start_time`, `end_time`, `max_appointments`, `appointed_count`, `status`, `remark`)
VALUES
((SELECT id FROM t_user WHERE username = 'doctor1'), DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00', '12:00', 10, 0, 1, '上午门诊'),
((SELECT id FROM t_user WHERE username = 'doctor1'), DATE_ADD(CURDATE(), INTERVAL 3 DAY), '14:00', '17:00', 10, 0, 1, '下午门诊'),
((SELECT id FROM t_user WHERE username = 'doctor1'), DATE_ADD(CURDATE(), INTERVAL 5 DAY), '09:00', '12:00', 10, 0, 1, '上午门诊'),
((SELECT id FROM t_user WHERE username = 'doctor2'), DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00', '12:00', 8, 0, 1, '上午门诊'),
((SELECT id FROM t_user WHERE username = 'doctor2'), DATE_ADD(CURDATE(), INTERVAL 3 DAY), '14:00', '17:00', 8, 0, 1, '下午门诊'),
((SELECT id FROM t_user WHERE username = 'doctor3'), DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00', '17:00', 6, 0, 1, '下午门诊'),
((SELECT id FROM t_user WHERE username = 'doctor3'), DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:00', '12:00', 6, 0, 1, '上午门诊');

-- 初始化预约数据
INSERT INTO `t_appointment` (`appointment_no`, `patient_id`, `doctor_id`, `schedule_id`, `appointment_date`, `appointment_time`, `symptoms`, `status`, `order_amount`, `payment_status`, `remark`)
VALUES
(CONCAT('AP', DATE_FORMAT(NOW(), '%Y%m%d'), '0001'), 
 (SELECT id FROM t_user WHERE username = 'patient1'), 
 (SELECT id FROM t_user WHERE username = 'doctor1'),
 (SELECT id FROM t_schedule WHERE doctor_id = (SELECT id FROM t_user WHERE username = 'doctor1') AND schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND start_time = '09:00'),
 DATE_ADD(CURDATE(), INTERVAL 1 DAY),
 '09:00-09:30',
 '右侧下颌大牙疼痛三天，疑似蛀牙',
 1, 
 50.00, 
 1,
 '初次就诊');

-- 更新已预约数量
UPDATE t_schedule SET appointed_count = appointed_count + 1 
WHERE id = (SELECT schedule_id FROM t_appointment WHERE appointment_no = CONCAT('AP', DATE_FORMAT(NOW(), '%Y%m%d'), '0001'));

-- 初始化支付记录
INSERT INTO `t_payment` (`payment_no`, `user_id`, `appointment_id`, `amount`, `payment_method`, `transaction_id`, `payment_status`, `payment_time`, `payment_type`, `remark`)
VALUES
(CONCAT('PAY', DATE_FORMAT(NOW(), '%Y%m%d'), '0001'),
 (SELECT id FROM t_user WHERE username = 'patient1'),
 (SELECT id FROM t_appointment WHERE appointment_no = CONCAT('AP', DATE_FORMAT(NOW(), '%Y%m%d'), '0001')),
 50.00,
 '微信支付',
 'WX123456789',
 1,
 NOW(),
 '挂号费',
 '挂号预约支付');

-- 初始化病历数据
INSERT INTO `t_medical_record` (`record_no`, `patient_id`, `doctor_id`, `appointment_id`, `record_date`, `diagnosis`, `treatment`, `prescription`, `next_visit_suggestion`, `teeth_condition`, `status`, `remark`)
VALUES
(CONCAT('MR', DATE_FORMAT(NOW(), '%Y%m%d'), '0001'),
 (SELECT id FROM t_user WHERE username = 'patient1'),
 (SELECT id FROM t_user WHERE username = 'doctor1'),
 (SELECT id FROM t_appointment WHERE appointment_no = CONCAT('AP', DATE_FORMAT(NOW(), '%Y%m%d'), '0001')),
 DATE_ADD(CURDATE(), INTERVAL 1 DAY),
 '右下第一磨牙深度龋齿',
 '进行了局部麻醉，去除龋坏组织，进行了根管预备',
 '口服抗生素3天，每日三次，饭后服用；局部用药按医嘱',
 '一周后复诊，进行根管治疗的下一步',
 '右下第一磨牙（46）牙体大面积缺损，牙髓暴露，叩痛(+)，冷热刺激痛(++)',
 1,
 '患者对治疗过程耐受良好');

-- 初始化系统配置
INSERT INTO `t_system_config` (`config_key`, `config_value`, `config_desc`)
VALUES
('system_name', '智能牙科就诊系统', '系统名称'),
('system_version', '1.0.0', '系统版本'),
('appointment_fee', '50.00', '默认挂号费'),
('enable_auto_confirm', 'true', '是否启用自动确认预约'),
('max_appointments_per_day', '3', '每位患者每天最大预约次数'),
('admin_email', 'admin@dental.com', '管理员邮箱'),
('system_maintenance_time', '每周日凌晨2:00-4:00', '系统维护时间');

-- 初始化系统日志
INSERT INTO `t_system_log` (`user_id`, `username`, `operation`, `method`, `params`, `ip`, `status`, `operation_time`)
VALUES
((SELECT id FROM t_user WHERE username = 'admin'), 'admin', '系统初始化', 'initSystemData', '{}', '127.0.0.1', 1, 0);
