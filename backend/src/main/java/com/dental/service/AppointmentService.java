package com.dental.service;

import com.dental.entity.Appointment;

import java.util.Date;
import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {
    
    /**
     * 创建预约
     */
    Appointment createAppointment(Appointment appointment);
    
    /**
     * 根据ID查询预约
     */
    Appointment getAppointmentById(Long id);
    
    /**
     * 更新预约状态
     */
    Appointment updateAppointmentStatus(Long id, Integer status);
    
    /**
     * 患者支付预约费用
     */
    Appointment payForAppointment(Long id, Double amount);
    
    /**
     * 患者支付预约费用（带支付方式）
     */
    Appointment payForAppointment(Long id, Double amount, String paymentMethod);
    
    /**
     * 查询患者的所有预约
     */
    List<Appointment> getPatientAppointments(Long patientId);
    
    /**
     * 查询医生的所有预约
     */
    List<Appointment> getDoctorAppointments(Long doctorId);
    
    /**
     * 查询医生在指定日期的预约
     */
    List<Appointment> getDoctorAppointmentsForDate(Long doctorId, Date date);
    
    /**
     * 取消预约
     */
    boolean cancelAppointment(Long id);
}
