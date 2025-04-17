package com.dental.service;

import com.dental.entity.Schedule;

import java.util.Date;
import java.util.List;

/**
 * 排班服务接口
 */
public interface ScheduleService {
    
    /**
     * 创建排班
     */
    Schedule createSchedule(Schedule schedule);
    
    /**
     * 根据ID查询排班
     */
    Schedule getScheduleById(Long id);
    
    /**
     * 更新排班信息
     */
    Schedule updateSchedule(Schedule schedule);
    
    /**
     * 查询医生的所有排班
     */
    List<Schedule> getDoctorSchedules(Long doctorId);
    
    /**
     * 查询医生在特定日期的排班
     */
    List<Schedule> getDoctorSchedulesByDate(Long doctorId, Date date);
    
    /**
     * 查询特定日期的所有医生排班
     */
    List<Schedule> getSchedulesByDate(Date date);
    
    /**
     * 查询特定日期范围内的排班
     */
    List<Schedule> getSchedulesByDateRange(Date startDate, Date endDate);
    
    /**
     * 更新排班状态
     */
    Schedule updateScheduleStatus(Long id, Integer status);
    
    /**
     * 批量创建医生排班
     */
    List<Schedule> batchCreateSchedules(Long doctorId, Date startDate, Date endDate, 
                                       String startTime, String endTime, Integer maxAppointments);
}
