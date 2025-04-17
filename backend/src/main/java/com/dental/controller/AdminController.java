package com.dental.controller;

import com.dental.common.Result;
import com.dental.dto.StatisticsDTO;
import com.dental.dto.SystemConfigDTO;
import com.dental.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 处理管理员相关的功能
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    /**
     * 获取统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<StatisticsDTO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            StatisticsDTO statistics = adminService.getStatistics(startDate, endDate);
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取系统设置
     * @return 系统设置列表
     */
    @GetMapping("/settings")
    public Result<List<SystemConfigDTO>> getSystemSettings() {
        try {
            List<SystemConfigDTO> settings = adminService.getSystemSettings();
            return Result.success(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取系统设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新系统设置
     * @param settings 系统设置
     * @return 更新结果
     */
    @PutMapping("/settings")
    public Result<Boolean> updateSystemSettings(@RequestBody List<SystemConfigDTO> settings) {
        try {
            boolean result = adminService.updateSystemSettings(settings);
            return Result.success("系统设置更新成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新系统设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取系统操作日志
     * @param page 页码
     * @param size 每页数量
     * @param username 用户名（可选）
     * @param operation 操作类型（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 日志列表
     */
    @GetMapping("/logs")
    public Result<Map<String, Object>> getSystemLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            Map<String, Object> logs = adminService.getSystemLogs(page, size, username, operation, startDate, endDate);
            return Result.success(logs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取系统日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取仪表盘数据
     * @return 仪表盘摘要数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> dashboardData = adminService.getDashboardData();
            return Result.success(dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取仪表盘数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取基本设置
     * @return 基本设置信息
     */
    @GetMapping("/settings/basic")
    public Result<Map<String, Object>> getBasicSettings() {
        try {
            Map<String, Object> settings = adminService.getBasicSettings();
            return Result.success(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取基本设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新基本设置
     * @param settings 基本设置信息
     * @return 更新结果
     */
    @PutMapping("/settings/basic")
    public Result<Boolean> updateBasicSettings(@RequestBody Map<String, Object> settings) {
        try {
            boolean result = adminService.updateBasicSettings(settings);
            return Result.success("基本设置更新成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新基本设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预约设置
     * @return 预约设置信息
     */
    @GetMapping("/settings/appointment")
    public Result<Map<String, Object>> getAppointmentSettings() {
        try {
            Map<String, Object> settings = adminService.getAppointmentSettings();
            return Result.success(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取预约设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新预约设置
     * @param settings 预约设置信息
     * @return 更新结果
     */
    @PutMapping("/settings/appointment")
    public Result<Boolean> updateAppointmentSettings(@RequestBody Map<String, Object> settings) {
        try {
            boolean result = adminService.updateAppointmentSettings(settings);
            return Result.success("预约设置更新成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新预约设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取通知设置
     * @return 通知设置信息
     */
    @GetMapping("/settings/notification")
    public Result<Map<String, Object>> getNotificationSettings() {
        try {
            Map<String, Object> settings = adminService.getNotificationSettings();
            return Result.success(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取通知设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取首页系统概览数据
     * @return 系统概览数据
     */
    @GetMapping("/home/overview")
    public Result<Map<String, Object>> getHomeOverview() {
        try {
            Map<String, Object> overviewData = adminService.getHomeOverview();
            return Result.success(overviewData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取首页系统概览数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新通知设置
     * @param settings 通知设置信息
     * @return 更新结果
     */
    @PutMapping("/settings/notification")
    public Result<Boolean> updateNotificationSettings(@RequestBody Map<String, Object> settings) {
        try {
            boolean result = adminService.updateNotificationSettings(settings);
            return Result.success("通知设置更新成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新通知设置失败: " + e.getMessage());
        }
    }
}
