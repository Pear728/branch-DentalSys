package com.dental.service;

import com.dental.dto.StatisticsDTO;
import com.dental.dto.SystemConfigDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 获取系统统计数据
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 统计数据DTO
     */
    StatisticsDTO getStatistics(Date startDate, Date endDate);
    
    /**
     * 获取系统设置列表
     * @return 系统设置列表
     */
    List<SystemConfigDTO> getSystemSettings();
    
    /**
     * 更新系统设置
     * @param settings 需要更新的设置列表
     * @return 是否更新成功
     */
    boolean updateSystemSettings(List<SystemConfigDTO> settings);
    
    /**
     * 获取系统日志
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @param username 用户名筛选（可选）
     * @param operation 操作类型筛选（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 包含日志列表和分页信息的Map
     */
    Map<String, Object> getSystemLogs(int page, int size, String username, String operation, Date startDate, Date endDate);
    
    /**
     * 获取管理员仪表盘数据
     * @return 仪表盘摘要数据
     */
    Map<String, Object> getDashboardData();
    
    /**
     * 获取基本设置
     * @return 基本设置信息
     */
    Map<String, Object> getBasicSettings();
    
    /**
     * 更新基本设置
     * @param settings 基本设置信息
     * @return 是否更新成功
     */
    boolean updateBasicSettings(Map<String, Object> settings);
    
    /**
     * 获取预约设置
     * @return 预约设置信息
     */
    Map<String, Object> getAppointmentSettings();
    
    /**
     * 更新预约设置
     * @param settings 预约设置信息
     * @return 是否更新成功
     */
    boolean updateAppointmentSettings(Map<String, Object> settings);
    
    /**
     * 获取通知设置
     * @return 通知设置信息
     */
    Map<String, Object> getNotificationSettings();
    
    /**
     * 更新通知设置
     * @param settings 通知设置信息
     * @return 是否更新成功
     */
    boolean updateNotificationSettings(Map<String, Object> settings);
    
    /**
     * 获取首页系统概览数据
     * @return 系统概览数据
     */
    Map<String, Object> getHomeOverview();
}
