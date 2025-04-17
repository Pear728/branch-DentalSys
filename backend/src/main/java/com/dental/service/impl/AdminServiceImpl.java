package com.dental.service.impl;

import com.dental.dto.StatisticsDTO;
import com.dental.dto.SystemConfigDTO;
import com.dental.entity.SystemConfig;
import com.dental.entity.SystemLog;
import com.dental.repository.*;
import com.dental.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public StatisticsDTO getStatistics(Date startDate, Date endDate) {
        // 初始化统计DTO
        StatisticsDTO statistics = new StatisticsDTO();
        
        // 设置默认日期范围（如果未指定）
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1); // 默认显示最近一个月
            startDate = calendar.getTime();
        }
        
        if (endDate == null) {
            endDate = new Date(); // 默认到当前日期
        }
        
        // 填充用户统计数据
        fillUserStatistics(statistics);
        
        // 填充预约统计数据
        fillAppointmentStatistics(statistics, startDate, endDate);
        
        // 填充收入统计数据
        fillIncomeStatistics(statistics, startDate, endDate);
        
        // 填充科室统计数据
        fillDepartmentStatistics(statistics);
        
        // 填充医生排名数据
        fillTopDoctorsStatistics(statistics);
        
        return statistics;
    }

    @Override
    public List<SystemConfigDTO> getSystemSettings() {
        List<SystemConfig> configList = systemConfigRepository.findAll();
        return configList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateSystemSettings(List<SystemConfigDTO> settings) {
        try {
            for (SystemConfigDTO dto : settings) {
                SystemConfig config;
                if (dto.getId() != null) {
                    // 更新已有配置
                    Optional<SystemConfig> optionalConfig = systemConfigRepository.findById(dto.getId());
                    if (!optionalConfig.isPresent()) {
                        continue; // 跳过不存在的配置
                    }
                    config = optionalConfig.get();
                    config.setConfigValue(dto.getConfigValue());
                    if (dto.getConfigDesc() != null) {
                        config.setConfigDesc(dto.getConfigDesc());
                    }
                } else {
                    // 新增配置
                    if (systemConfigRepository.existsByConfigKey(dto.getConfigKey())) {
                        // 已存在相同key的配置，跳过
                        continue;
                    }
                    config = new SystemConfig();
                    config.setConfigKey(dto.getConfigKey());
                    config.setConfigValue(dto.getConfigValue());
                    config.setConfigDesc(dto.getConfigDesc());
                }
                systemConfigRepository.save(config);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Object> getSystemLogs(int page, int size, String username, String operation, Date startDate, Date endDate) {
        // 将页码转换为0基数
        page = Math.max(0, page - 1);
        
        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size);
        
        // 执行条件查询
        Page<SystemLog> logPage = systemLogRepository.findByConditions(username, operation, startDate, endDate, pageable);
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("logs", logPage.getContent());
        result.put("totalElements", logPage.getTotalElements());
        result.put("totalPages", logPage.getTotalPages());
        result.put("currentPage", page + 1);
        result.put("size", size);
        
        return result;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 获取用户总数
        long totalUsers = userRepository.count();
        dashboard.put("totalUsers", totalUsers);
        
        // 获取今日新增用户数
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date today = calendar.getTime();
        
        long newUsersToday = userRepository.countByCreateTimeAfter(today);
        dashboard.put("newUsersToday", newUsersToday);
        
        // 获取预约总数
        long totalAppointments = appointmentRepository.count();
        dashboard.put("totalAppointments", totalAppointments);
        
        // 获取今日预约数
        long appointmentsToday = appointmentRepository.countByAppointmentDate(today);
        dashboard.put("appointmentsToday", appointmentsToday);
        
        // 获取待处理预约数
        long pendingAppointments = appointmentRepository.countByStatus(0); // 假设状态0为待处理
        dashboard.put("pendingAppointments", pendingAppointments);
        
        // 获取医生数量
        long doctorCount = userRepository.countByRole("DOCTOR");
        dashboard.put("doctorCount", doctorCount);
        
        // 获取收入统计
        BigDecimal totalIncome = getTotalIncome();
        dashboard.put("totalIncome", totalIncome);
        
        // 获取本月收入
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date firstDayOfMonth = calendar.getTime();
        
        BigDecimal monthlyIncome = getIncomeInPeriod(firstDayOfMonth, new Date());
        dashboard.put("monthlyIncome", monthlyIncome);
        
        return dashboard;
    }
    
    // 辅助方法: 填充用户统计数据
    private void fillUserStatistics(StatisticsDTO statistics) {
        // 查询总用户数
        long totalUsers = userRepository.count();
        statistics.setTotalUsers((int) totalUsers);
        
        // 查询患者数量
        long patientCount = userRepository.countByRole("PATIENT");
        statistics.setTotalPatients((int) patientCount);
        
        // 查询医生数量
        long doctorCount = userRepository.countByRole("DOCTOR");
        statistics.setTotalDoctors((int) doctorCount);
        
        // 获取今日新增用户数
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date today = calendar.getTime();
        
        long newUsersToday = userRepository.countByCreateTimeAfter(today);
        statistics.setNewUsersToday((int) newUsersToday);
        
        // 获取本周新增用户数
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date firstDayOfWeek = calendar.getTime();
        
        long newUsersWeek = userRepository.countByCreateTimeAfter(firstDayOfWeek);
        statistics.setNewUsersWeek((int) newUsersWeek);
        
        // 获取本月新增用户数
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        
        long newUsersMonth = userRepository.countByCreateTimeAfter(firstDayOfMonth);
        statistics.setNewUsersMonth((int) newUsersMonth);
        
        // 用户增长趋势数据
        statistics.setUserGrowthData(getUserGrowthData());
    }
    
    // 辅助方法: 填充预约统计数据
    private void fillAppointmentStatistics(StatisticsDTO statistics, Date startDate, Date endDate) {
        // 查询总预约数
        long totalAppointments = appointmentRepository.count();
        statistics.setTotalAppointments((int) totalAppointments);
        
        // 查询已完成预约数（状态值需要根据实际情况调整）
        long completedAppointments = appointmentRepository.countByStatus(2); // 假设状态2为已完成
        statistics.setCompletedAppointments((int) completedAppointments);
        
        // 查询已取消预约数
        long cancelledAppointments = appointmentRepository.countByStatus(3); // 假设状态3为已取消
        statistics.setCancelledAppointments((int) cancelledAppointments);
        
        // 获取今日预约数
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date today = calendar.getTime();
        
        long appointmentsToday = appointmentRepository.countByAppointmentDate(today);
        statistics.setAppointmentsToday((int) appointmentsToday);
        
        // 获取本周预约数
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date firstDayOfWeek = calendar.getTime();
        
        long appointmentsWeek = appointmentRepository.countByAppointmentDateBetween(firstDayOfWeek, new Date());
        statistics.setAppointmentsWeek((int) appointmentsWeek);
        
        // 获取本月预约数
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        
        long appointmentsMonth = appointmentRepository.countByAppointmentDateBetween(firstDayOfMonth, new Date());
        statistics.setAppointmentsMonth((int) appointmentsMonth);
        
        // 预约趋势数据
        statistics.setAppointmentTrendData(getAppointmentTrendData(startDate, endDate));
    }
    
    // 辅助方法: 填充收入统计数据
    private void fillIncomeStatistics(StatisticsDTO statistics, Date startDate, Date endDate) {
        // 获取总收入
        BigDecimal totalIncome = getTotalIncome();
        statistics.setTotalIncome(totalIncome);
        
        // 获取今日收入
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date today = calendar.getTime();
        
        BigDecimal incomeToday = getIncomeInPeriod(today, new Date());
        statistics.setIncomeToday(incomeToday);
        
        // 获取本周收入
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date firstDayOfWeek = calendar.getTime();
        
        BigDecimal incomeWeek = getIncomeInPeriod(firstDayOfWeek, new Date());
        statistics.setIncomeWeek(incomeWeek);
        
        // 获取本月收入
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        
        BigDecimal incomeMonth = getIncomeInPeriod(firstDayOfMonth, new Date());
        statistics.setIncomeMonth(incomeMonth);
        
        // 收入趋势数据
        statistics.setIncomeTrendData(getIncomeTrendData(startDate, endDate));
    }
    
    // 辅助方法: 填充科室统计数据
    private void fillDepartmentStatistics(StatisticsDTO statistics) {
        List<Map<String, Object>> departmentData = new ArrayList<>();
        
        // 获取科室统计数据
        String query = "SELECT d.department, COUNT(d.id) as doctorCount, " +
                      "(SELECT COUNT(a.id) FROM t_appointment a " +
                      "JOIN t_user u ON a.doctor_id = u.id " +
                      "JOIN t_doctor_info di ON u.id = di.user_id " +
                      "WHERE di.department = d.department) as appointmentCount " +
                      "FROM t_doctor_info d GROUP BY d.department";
        
        try {
            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(query).getResultList();
            
            for (Object[] result : results) {
                Map<String, Object> dept = new HashMap<>();
                dept.put("department", result[0]);
                dept.put("doctorCount", result[1]);
                dept.put("appointmentCount", result[2]);
                departmentData.add(dept);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        statistics.setDepartmentData(departmentData);
    }
    
    // 辅助方法: 填充医生排名数据
    private void fillTopDoctorsStatistics(StatisticsDTO statistics) {
        List<Map<String, Object>> topDoctors = new ArrayList<>();
        
        // 获取预约量排名前十的医生
        String query = "SELECT u.id, u.username, u.real_name, d.department, d.professional_title, " +
                      "COUNT(a.id) as appointmentCount " +
                      "FROM t_user u " +
                      "JOIN t_doctor_info d ON u.id = d.user_id " +
                      "LEFT JOIN t_appointment a ON u.id = a.doctor_id " +
                      "WHERE u.role = 'DOCTOR' " +
                      "GROUP BY u.id, u.username, u.real_name, d.department, d.professional_title " +
                      "ORDER BY appointmentCount DESC LIMIT 10";
        
        try {
            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(query).getResultList();
            
            for (Object[] result : results) {
                Map<String, Object> doctor = new HashMap<>();
                doctor.put("id", result[0]);
                doctor.put("username", result[1]);
                doctor.put("realName", result[2]);
                doctor.put("department", result[3]);
                doctor.put("title", result[4]);
                doctor.put("appointmentCount", result[5]);
                topDoctors.add(doctor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        statistics.setTopDoctors(topDoctors);
    }
    
    // 辅助方法: 获取用户增长趋势数据
    private List<Map<String, Object>> getUserGrowthData() {
        List<Map<String, Object>> growthData = new ArrayList<>();
        
        // 查询近30天的用户注册量
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date startDate = calendar.getTime();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // 准备SQL查询
        String sql = "SELECT DATE(create_time) as date, COUNT(*) as count " +
                     "FROM t_user " +
                     "WHERE create_time >= :startDate " +
                     "GROUP BY DATE(create_time) " +
                     "ORDER BY date";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        for (Object[] result : results) {
            Map<String, Object> dayData = new HashMap<>();
            // 第一列是日期，第二列是数量
            Date date = (Date) result[0];
            Long count = ((Number) result[1]).longValue();
            
            dayData.put("date", dateFormat.format(date));
            dayData.put("count", count);
            growthData.add(dayData);
        }
        
        return growthData;
    }
    
    // 辅助方法: 获取预约趋势数据
    private List<Map<String, Object>> getAppointmentTrendData(Date startDate, Date endDate) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // 准备SQL查询
        String sql = "SELECT DATE(appointment_date) as date, COUNT(*) as count " +
                     "FROM t_appointment " +
                     "WHERE appointment_date BETWEEN :startDate AND :endDate " +
                     "GROUP BY DATE(appointment_date) " +
                     "ORDER BY date";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        for (Object[] result : results) {
            Map<String, Object> dayData = new HashMap<>();
            // 第一列是日期，第二列是数量
            Date date = (Date) result[0];
            Long count = ((Number) result[1]).longValue();
            
            dayData.put("date", dateFormat.format(date));
            dayData.put("count", count);
            trendData.add(dayData);
        }
        
        return trendData;
    }
    
    // 辅助方法: 获取收入趋势数据
    private List<Map<String, Object>> getIncomeTrendData(Date startDate, Date endDate) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // 准备SQL查询
        String sql = "SELECT DATE(payment_time) as date, SUM(amount) as amount " +
                     "FROM t_payment " +
                     "WHERE payment_status = 1 AND payment_time BETWEEN :startDate AND :endDate " +
                     "GROUP BY DATE(payment_time) " +
                     "ORDER BY date";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        for (Object[] result : results) {
            Map<String, Object> dayData = new HashMap<>();
            // 第一列是日期，第二列是金额
            Date date = (Date) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            
            dayData.put("date", dateFormat.format(date));
            dayData.put("amount", amount);
            trendData.add(dayData);
        }
        
        return trendData;
    }
    
    // 辅助方法: 获取总收入
    private BigDecimal getTotalIncome() {
        try {
            String sql = "SELECT COALESCE(SUM(amount), 0) FROM t_payment WHERE payment_status = 1";
            Query query = entityManager.createNativeQuery(sql);
            
            Object result = query.getSingleResult();
            if (result == null) {
                return BigDecimal.ZERO;
            }
            
            // 处理不同的数字类型转换成BigDecimal
            if (result instanceof BigDecimal) {
                return (BigDecimal) result;
            } else if (result instanceof Double) {
                return BigDecimal.valueOf((Double) result);
            } else if (result instanceof Long) {
                return BigDecimal.valueOf((Long) result);
            } else if (result instanceof Integer) {
                return BigDecimal.valueOf((Integer) result);
            } else {
                // 尝试使用toString后解析为BigDecimal
                return new BigDecimal(result.toString());
            }
        } catch (Exception e) {
            // 出现异常时记录并返回零值
            System.err.println("获取收入总额时出错: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    // 辅助方法: 获取指定时间段内的收入
    private BigDecimal getIncomeInPeriod(Date startDate, Date endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM t_payment WHERE payment_status = 1 AND payment_time BETWEEN :startDate AND :endDate";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        Object result = query.getSingleResult();
        return (result != null) ? (BigDecimal) result : BigDecimal.ZERO;
    }
    
    // 辅助方法: DTO转换
    private SystemConfigDTO convertToDTO(SystemConfig config) {
        SystemConfigDTO dto = new SystemConfigDTO();
        dto.setId(config.getId());
        dto.setConfigKey(config.getConfigKey());
        dto.setConfigValue(config.getConfigValue());
        dto.setConfigDesc(config.getConfigDesc());
        return dto;
    }
    
    @Override
    public Map<String, Object> getBasicSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // 获取诊所基本设置信息
        settings.put("clinicName", getConfigValue("clinic.name", "口腔医院"));
        settings.put("clinicPhone", getConfigValue("clinic.phone", "010-12345678"));
        settings.put("clinicAddress", getConfigValue("clinic.address", "北京市海淀区中关村"));
        settings.put("clinicDescription", getConfigValue("clinic.description", "专业口腔医疗服务"));
        
        // 获取营业时间
        String openTime = getConfigValue("clinic.openTime", "08:30");
        String closeTime = getConfigValue("clinic.closeTime", "17:30");
        settings.put("openTime", openTime);
        settings.put("closeTime", closeTime);
        
        return settings;
    }
    
    @Override
    @Transactional
    public boolean updateBasicSettings(Map<String, Object> settings) {
        try {
            // 更新诊所基本信息
            updateConfig("clinic.name", String.valueOf(settings.get("clinicName")), "诊所名称");
            updateConfig("clinic.phone", String.valueOf(settings.get("clinicPhone")), "诊所电话");
            updateConfig("clinic.address", String.valueOf(settings.get("clinicAddress")), "诊所地址");
            updateConfig("clinic.description", String.valueOf(settings.get("clinicDescription")), "诊所简介");
            
            // 更新营业时间
            updateConfig("clinic.openTime", String.valueOf(settings.get("openTime")), "开始营业时间");
            updateConfig("clinic.closeTime", String.valueOf(settings.get("closeTime")), "结束营业时间");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getAppointmentSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // 获取预约设置信息
        settings.put("timeSlotDuration", Integer.parseInt(getConfigValue("appointment.timeSlotDuration", "30")));
        settings.put("maxDaysInAdvance", Integer.parseInt(getConfigValue("appointment.maxDaysInAdvance", "30")));
        settings.put("maxQueueSize", Integer.parseInt(getConfigValue("appointment.maxQueueSize", "5")));
        settings.put("cancelThreshold", Integer.parseInt(getConfigValue("appointment.cancelThreshold", "24")));
        
        return settings;
    }
    
    @Override
    @Transactional
    public boolean updateAppointmentSettings(Map<String, Object> settings) {
        try {
            // 更新预约设置
            updateConfig("appointment.timeSlotDuration", String.valueOf(settings.get("timeSlotDuration")), "预约时间间隔(分钟)");
            updateConfig("appointment.maxDaysInAdvance", String.valueOf(settings.get("maxDaysInAdvance")), "最大提前预约天数");
            updateConfig("appointment.maxQueueSize", String.valueOf(settings.get("maxQueueSize")), "最大排队人数");
            updateConfig("appointment.cancelThreshold", String.valueOf(settings.get("cancelThreshold")), "允许取消时间(小时)");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getNotificationSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // 获取通知设置信息
        settings.put("smsEnabled", Boolean.parseBoolean(getConfigValue("notification.smsEnabled", "false")));
        settings.put("smsNotificationTypes", getConfigValue("notification.smsTypes", "appointment,reminder"));
        settings.put("reminderTime", Integer.parseInt(getConfigValue("notification.reminderTime", "60")));
        settings.put("emailEnabled", Boolean.parseBoolean(getConfigValue("notification.emailEnabled", "false")));
        settings.put("emailNotificationTypes", getConfigValue("notification.emailTypes", "appointment,reminder,system"));
        
        return settings;
    }
    
    @Override
    @Transactional
    public boolean updateNotificationSettings(Map<String, Object> settings) {
        try {
            // 更新通知设置
            updateConfig("notification.smsEnabled", String.valueOf(settings.get("smsEnabled")), "是否启用短信通知");
            updateConfig("notification.smsTypes", String.valueOf(settings.get("smsNotificationTypes")), "短信通知类型");
            updateConfig("notification.reminderTime", String.valueOf(settings.get("reminderTime")), "提醒时间(分钟)");
            updateConfig("notification.emailEnabled", String.valueOf(settings.get("emailEnabled")), "是否启用邮件通知");
            updateConfig("notification.emailTypes", String.valueOf(settings.get("emailNotificationTypes")), "邮件通知类型");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取配置值，如果不存在则返回默认值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        Optional<SystemConfig> configOpt = systemConfigRepository.findByConfigKey(key);
        return configOpt.map(SystemConfig::getConfigValue).orElse(defaultValue);
    }
    
    /**
     * 更新或创建配置
     * @param key 配置键
     * @param value 配置值
     * @param description 配置描述
     */
    private void updateConfig(String key, String value, String description) {
        Optional<SystemConfig> configOpt = systemConfigRepository.findByConfigKey(key);
        SystemConfig config = configOpt.orElse(new SystemConfig());
        
        if (!configOpt.isPresent()) {
            // 创建新配置
            config.setConfigKey(key);
        }
        config.setConfigValue(value);
        config.setConfigDesc(description);
        systemConfigRepository.save(config);
    }
    
    @Override
    public Map<String, Object> getHomeOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 1. 获取用户总数
        long totalUsers = userRepository.count();
        overview.put("totalUsers", totalUsers);
        
        // 2. 获取医生数量
        long doctorCount = userRepository.countByRole("DOCTOR");
        overview.put("doctorCount", doctorCount);
        
        // 3. 获取今日预约数
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        
        long todayAppointments = appointmentRepository.countByAppointmentDateBetween(
                today.getTime(), tomorrow.getTime());
        overview.put("todayAppointments", todayAppointments);
        
        // 4. 获取病历总数
        String sqlMedicalRecords = "SELECT COUNT(*) FROM t_medical_record";
        Query queryMedicalRecords = entityManager.createNativeQuery(sqlMedicalRecords);
        long totalRecords = ((Number) queryMedicalRecords.getSingleResult()).longValue();
        overview.put("totalRecords", totalRecords);
        
        // 5. 获取收入总额
        BigDecimal totalIncome = getTotalIncome();
        overview.put("totalIncome", totalIncome);
        
        // 6. 获取本月预约数
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfMonth.set(Calendar.MINUTE, 0);
        firstDayOfMonth.set(Calendar.SECOND, 0);
        firstDayOfMonth.set(Calendar.MILLISECOND, 0);
        
        Calendar firstDayOfNextMonth = Calendar.getInstance();
        firstDayOfNextMonth.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfNextMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfNextMonth.set(Calendar.MINUTE, 0);
        firstDayOfNextMonth.set(Calendar.SECOND, 0);
        firstDayOfNextMonth.set(Calendar.MILLISECOND, 0);
        firstDayOfNextMonth.add(Calendar.MONTH, 1);
        
        long monthlyAppointments = appointmentRepository.countByAppointmentDateBetween(
                firstDayOfMonth.getTime(), firstDayOfNextMonth.getTime());
        overview.put("monthlyAppointments", monthlyAppointments);
        
        // 7. 获取近7天预约趋势数据
        Calendar sevenDaysAgo = Calendar.getInstance();
        sevenDaysAgo.add(Calendar.DAY_OF_MONTH, -6);
        sevenDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
        sevenDaysAgo.set(Calendar.MINUTE, 0);
        sevenDaysAgo.set(Calendar.SECOND, 0);
        sevenDaysAgo.set(Calendar.MILLISECOND, 0);
        
        List<Map<String, Object>> appointmentTrend = getAppointmentTrendData(sevenDaysAgo.getTime(), tomorrow.getTime());
        overview.put("appointmentTrend", appointmentTrend);
        
        return overview;
    }
}
