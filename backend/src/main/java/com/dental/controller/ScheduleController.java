package com.dental.controller;

import com.dental.common.Result;
import com.dental.entity.Schedule;
import com.dental.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 排班控制器
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    
    /**
     * 创建排班
     */
    @PostMapping
    public Result<Schedule> createSchedule(@RequestBody Schedule schedule) {
        try {
            // 确保设置初始值
            if (schedule.getAppointedCount() == null) {
                schedule.setAppointedCount(0);
            }
            
            Schedule createdSchedule = scheduleService.createSchedule(schedule);
            return Result.success("排班创建成功", createdSchedule);
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息便于调试
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取排班详情
     */
    @GetMapping("/{id}")
    public Result<Schedule> getScheduleById(@PathVariable Long id) {
        try {
            Schedule schedule = scheduleService.getScheduleById(id);
            return Result.success(schedule);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 更新排班信息
     */
    @PutMapping
    public Result<Schedule> updateSchedule(@RequestBody Schedule schedule) {
        try {
            Schedule updatedSchedule = scheduleService.updateSchedule(schedule);
            return Result.success("排班更新成功", updatedSchedule);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取医生的所有排班
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<Schedule>> getDoctorSchedules(@PathVariable Long doctorId) {
        try {
            List<Schedule> schedules = scheduleService.getDoctorSchedules(doctorId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取医生在特定日期的排班
     */
    @GetMapping("/doctor/{doctorId}/date")
    public Result<List<Schedule>> getDoctorSchedulesByDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            List<Schedule> schedules = scheduleService.getDoctorSchedulesByDate(doctorId, date);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取特定日期的所有医生排班
     */
    @GetMapping("/date")
    public Result<List<Schedule>> getSchedulesByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByDate(date);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取特定日期范围内的排班
     */
    @GetMapping("/date-range")
    public Result<List<Schedule>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByDateRange(startDate, endDate);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 更新排班状态
     */
    @PutMapping("/{id}/status/{status}")
    public Result<Schedule> updateScheduleStatus(@PathVariable Long id, @PathVariable Integer status) {
        try {
            Schedule schedule = scheduleService.updateScheduleStatus(id, status);
            return Result.success("状态更新成功", schedule);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 批量创建排班
     */
    @PostMapping("/batch")
    public Result<List<Schedule>> batchCreateSchedules(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Integer maxAppointments) {
        try {
            System.out.println("开始批量创建排班，从" + startDate + "到" + endDate);
            
            // 创建测试排班对象，检查是否可以正常设置已预约人数
            Schedule testSchedule = new Schedule();
            testSchedule.setAppointedCount(0); // 明确设置为0
            Integer testCount = testSchedule.getAppointedCount();
            System.out.println("测试排班已预约人数: " + testCount + ", 类型: " + (testCount != null ? testCount.getClass().getName() : "null"));
            
            // 调用服务层创建排班
            List<Schedule> schedules = scheduleService.batchCreateSchedules(
                    doctorId, startDate, endDate, startTime, endTime, maxAppointments);
            
            // 确保所有排班都设置了已预约人数
            for (Schedule schedule : schedules) {
                if (schedule.getAppointedCount() == null) {
                    System.out.println("设置排班 ID: " + schedule.getId() + " 的已预约人数为0");
                    schedule.setAppointedCount(0);
                    scheduleService.updateSchedule(schedule);
                }
            }
            
            System.out.println("批量创建排班成功，共创建" + schedules.size() + "条排班记录");
            return Result.success("批量创建排班成功", schedules);
        } catch (Exception e) {
            System.err.println("批量创建排班失败: " + e.getMessage());
            e.printStackTrace();
            return Result.fail("\u6279\u91cf\u521b\u5efa\u6392\u73ed\u5931\u8d25: " + e.getMessage());
        }
    }
}
