package com.dental.repository;

import com.dental.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 排班数据访问接口
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    /**
     * 查询医生的所有排班
     */
    List<Schedule> findByDoctorIdOrderByScheduleDateDesc(Long doctorId);
    
    /**
     * 查询特定日期的所有医生排班
     */
    List<Schedule> findByScheduleDateOrderByDoctorId(Date scheduleDate);
    
    /**
     * 查询医生在特定日期的排班
     */
    List<Schedule> findByDoctorIdAndScheduleDate(Long doctorId, Date scheduleDate);
    
    /**
     * 查询可预约状态的排班
     */
    List<Schedule> findByStatusOrderByScheduleDate(Integer status);
    
    /**
     * 查询特定日期范围内的排班
     */
    List<Schedule> findByScheduleDateBetweenOrderByScheduleDateAsc(Date startDate, Date endDate);
}
