package com.dental.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计数据DTO
 * 用于管理员查看系统统计数据
 */
public class StatisticsDTO {
    // 用户统计
    private Integer totalUsers;          // 总用户数
    private Integer totalPatients;       // 患者数量
    private Integer totalDoctors;        // 医生数量 
    private Integer newUsersToday;       // 今日新增用户
    private Integer newUsersWeek;        // 本周新增用户
    private Integer newUsersMonth;       // 本月新增用户
    private List<Map<String, Object>> userGrowthData; // 用户增长趋势数据
    
    // 预约统计
    private Integer totalAppointments;    // 总预约数  
    private Integer completedAppointments; // 已完成预约数
    private Integer cancelledAppointments; // 已取消预约数
    private Integer appointmentsToday;     // 今日预约数
    private Integer appointmentsWeek;      // 本周预约数
    private Integer appointmentsMonth;     // 本月预约数
    private List<Map<String, Object>> appointmentTrendData; // 预约趋势数据
    
    // 收入统计
    private BigDecimal totalIncome;           // 总收入
    private BigDecimal incomeToday;           // 今日收入
    private BigDecimal incomeWeek;            // 本周收入
    private BigDecimal incomeMonth;           // 本月收入
    private List<Map<String, Object>> incomeTrendData; // 收入趋势数据
    
    // 科室统计
    private List<Map<String, Object>> departmentData; // 各科室数据统计
    
    // 医生统计
    private List<Map<String, Object>> topDoctors; // 预约量排名前十的医生
    
    // getter and setter 方法
    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Integer totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Integer getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Integer totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Integer getNewUsersToday() {
        return newUsersToday;
    }

    public void setNewUsersToday(Integer newUsersToday) {
        this.newUsersToday = newUsersToday;
    }

    public Integer getNewUsersWeek() {
        return newUsersWeek;
    }

    public void setNewUsersWeek(Integer newUsersWeek) {
        this.newUsersWeek = newUsersWeek;
    }

    public Integer getNewUsersMonth() {
        return newUsersMonth;
    }

    public void setNewUsersMonth(Integer newUsersMonth) {
        this.newUsersMonth = newUsersMonth;
    }

    public List<Map<String, Object>> getUserGrowthData() {
        return userGrowthData;
    }

    public void setUserGrowthData(List<Map<String, Object>> userGrowthData) {
        this.userGrowthData = userGrowthData;
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Integer getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(Integer completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public Integer getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(Integer cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public Integer getAppointmentsToday() {
        return appointmentsToday;
    }

    public void setAppointmentsToday(Integer appointmentsToday) {
        this.appointmentsToday = appointmentsToday;
    }

    public Integer getAppointmentsWeek() {
        return appointmentsWeek;
    }

    public void setAppointmentsWeek(Integer appointmentsWeek) {
        this.appointmentsWeek = appointmentsWeek;
    }

    public Integer getAppointmentsMonth() {
        return appointmentsMonth;
    }

    public void setAppointmentsMonth(Integer appointmentsMonth) {
        this.appointmentsMonth = appointmentsMonth;
    }

    public List<Map<String, Object>> getAppointmentTrendData() {
        return appointmentTrendData;
    }

    public void setAppointmentTrendData(List<Map<String, Object>> appointmentTrendData) {
        this.appointmentTrendData = appointmentTrendData;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getIncomeToday() {
        return incomeToday;
    }

    public void setIncomeToday(BigDecimal incomeToday) {
        this.incomeToday = incomeToday;
    }

    public BigDecimal getIncomeWeek() {
        return incomeWeek;
    }

    public void setIncomeWeek(BigDecimal incomeWeek) {
        this.incomeWeek = incomeWeek;
    }

    public BigDecimal getIncomeMonth() {
        return incomeMonth;
    }

    public void setIncomeMonth(BigDecimal incomeMonth) {
        this.incomeMonth = incomeMonth;
    }

    public List<Map<String, Object>> getIncomeTrendData() {
        return incomeTrendData;
    }

    public void setIncomeTrendData(List<Map<String, Object>> incomeTrendData) {
        this.incomeTrendData = incomeTrendData;
    }

    public List<Map<String, Object>> getDepartmentData() {
        return departmentData;
    }

    public void setDepartmentData(List<Map<String, Object>> departmentData) {
        this.departmentData = departmentData;
    }

    public List<Map<String, Object>> getTopDoctors() {
        return topDoctors;
    }

    public void setTopDoctors(List<Map<String, Object>> topDoctors) {
        this.topDoctors = topDoctors;
    }
}
