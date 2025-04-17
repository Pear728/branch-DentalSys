package com.dental.service.impl;

import com.dental.entity.Schedule;
import com.dental.repository.ScheduleRepository;
import com.dental.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 排班服务实现类
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        try {
            // 检查同一医生同一天是否已有排班
            List<Schedule> existingSchedules = scheduleRepository.findByDoctorIdAndScheduleDate(
                    schedule.getDoctorId(), schedule.getScheduleDate());
            
            // 如果已有排班，不允许重复创建
            if (!existingSchedules.isEmpty()) {
                throw new RuntimeException("该医生在所选日期已有排班安排");
            }
            
            // 设置默认状态为可预约
            schedule.setStatus(1);
            
            // 设置已预约人数初始值为0 - 这是关键步骤
            if (schedule.getAppointedCount() == null) {
                schedule.setAppointedCount(0);
            }
            
            // 再次确认已预约人数已设置
            Integer appointedCount = schedule.getAppointedCount();
            if (appointedCount == null) {
                appointedCount = 0;
                schedule.setAppointedCount(appointedCount);
            }
            
            // 打印日志检查是否正确设置了已预约人数
            System.out.println("创建排班，已预约人数: " + schedule.getAppointedCount());
            
            // 保存排班信息
            Schedule savedSchedule = scheduleRepository.save(schedule);
            
            // 再次确认保存后的已预约人数
            if (savedSchedule.getAppointedCount() == null) {
                System.out.println("保存后的已预约人数为空，重新设置为0");
                savedSchedule.setAppointedCount(0);
                savedSchedule = scheduleRepository.save(savedSchedule);
            }
            
            return savedSchedule;
        } catch (Exception e) {
            System.err.println("创建排班失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建排班失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班记录不存在"));
    }

    @Override
    public Schedule updateSchedule(Schedule schedule) {
        // 验证排班记录是否存在
        Schedule existingSchedule = getScheduleById(schedule.getId());
        
        // 更新排班信息
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> getDoctorSchedules(Long doctorId) {
        return scheduleRepository.findByDoctorIdOrderByScheduleDateDesc(doctorId);
    }

    @Override
    public List<Schedule> getDoctorSchedulesByDate(Long doctorId, Date date) {
        return scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, date);
    }

    @Override
    public List<Schedule> getSchedulesByDate(Date date) {
        return scheduleRepository.findByScheduleDateOrderByDoctorId(date);
    }

    @Override
    public List<Schedule> getSchedulesByDateRange(Date startDate, Date endDate) {
        return scheduleRepository.findByScheduleDateBetweenOrderByScheduleDateAsc(startDate, endDate);
    }

    @Override
    public Schedule updateScheduleStatus(Long id, Integer status) {
        // 获取排班信息
        Schedule schedule = getScheduleById(id);
        
        // 更新状态
        schedule.setStatus(status);
        
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public List<Schedule> batchCreateSchedules(Long doctorId, Date startDate, Date endDate, 
                                              String startTime, String endTime, Integer maxAppointments) {
        List<Schedule> createdSchedules = new ArrayList<>();
        
        try {
            // 计算日期区间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            
            System.out.println("开始批量创建排班，从" + startDate + "到" + endDate);
            
            // 遍历每一天
            while (!calendar.after(endCalendar)) {
                Date currentDate = calendar.getTime();
                
                // 检查当前日期是否已有排班
                List<Schedule> existingSchedules = scheduleRepository.findByDoctorIdAndScheduleDate(
                        doctorId, currentDate);
                
                // 如果没有排班，则创建新排班
                if (existingSchedules.isEmpty()) {
                    Schedule schedule = new Schedule();
                    schedule.setDoctorId(doctorId);
                    schedule.setScheduleDate(currentDate);
                    schedule.setStartTime(startTime);
                    schedule.setEndTime(endTime);
                    schedule.setMaxAppointments(maxAppointments);
                    schedule.setStatus(1);
                    
                    // 明确设置已预约人数为0 - 这是关键步骤
                    schedule.setAppointedCount(0);
                    
                    // 再次确认已预约人数已设置
                    if (schedule.getAppointedCount() == null) {
                        schedule.setAppointedCount(0);
                    }
                    
                    System.out.println("创建排班日期: " + currentDate + ", 已预约人数: " + schedule.getAppointedCount());
                    
                    // 保存排班信息
                    Schedule savedSchedule = scheduleRepository.save(schedule);
                    
                    // 确认保存后的已预约人数
                    if (savedSchedule.getAppointedCount() == null) {
                        System.out.println("警告: 保存后的已预约人数为空，重新设置为0");
                        savedSchedule.setAppointedCount(0);
                        savedSchedule = scheduleRepository.save(savedSchedule);
                    }
                    
                    createdSchedules.add(savedSchedule);
                }
                
                // 日期加1天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            System.out.println("批量创建排班完成，共创建" + createdSchedules.size() + "条排班记录");
        } catch (Exception e) {
            System.err.println("批量创建排班失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("批量创建排班失败: " + e.getMessage(), e);
        }
        
        return createdSchedules;
    }
}
