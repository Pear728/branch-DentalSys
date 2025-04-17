package com.dental.repository;

import com.dental.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 预约数据访问接口
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * 查询患者的所有预约
     */
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    
    /**
     * 查询医生的所有预约
     */
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);
    
    /**
     * 查询医生在指定日期的预约
     */
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, Date appointmentDate);
    
    /**
     * 查询指定状态的预约
     */
    List<Appointment> findByStatus(Integer status);
    
    /**
     * 查询患者的特定状态预约
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, Integer status);
    
    /**
     * 查询医生的特定状态预约
     */
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, Integer status);
    
    /**
     * 计算指定状态的预约数量
     * @param status 预约状态
     * @return 预约数量
     */
    long countByStatus(int status);
    
    /**
     * 计算指定日期的预约数量
     * @param appointmentDate 预约日期
     * @return 预约数量
     */
    long countByAppointmentDate(Date appointmentDate);
    
    /**
     * 查询指定日期区间的预约数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预约数量
     */
    long countByAppointmentDateBetween(Date startDate, Date endDate);
    
    /**
     * 查询指定支付状态的预约
     * @param paymentStatus 支付状态：0-未支付，1-已支付
     * @return 预约列表
     */
    List<Appointment> findByPaymentStatus(Integer paymentStatus);
}
