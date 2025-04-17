package com.dental.controller;

import com.dental.common.Result;
import com.dental.entity.Appointment;
import com.dental.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    /**
     * 创建预约
     */
    @PostMapping
    public Result<Appointment> createAppointment(@RequestBody Appointment appointment) {
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            return Result.success("预约成功", createdAppointment);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<Appointment> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return Result.success(appointment);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 更新预约状态
     */
    @PutMapping("/{id}/status/{status}")
    public Result<Appointment> updateAppointmentStatus(@PathVariable Long id, @PathVariable Integer status) {
        try {
            Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
            return Result.success("状态更新成功", appointment);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 支付预约费用
     */
    @PutMapping("/{id}/pay")
    public Result<Appointment> payForAppointment(
            @PathVariable Long id, 
            @RequestParam Double amount,
            @RequestParam(required = false, defaultValue = "online") String paymentMethod) {
        try {
            Appointment appointment = appointmentService.payForAppointment(id, amount, paymentMethod);
            return Result.success("支付成功", appointment);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取患者的所有预约
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<Appointment>> getPatientAppointments(@PathVariable Long patientId) {
        try {
            List<Appointment> appointments = appointmentService.getPatientAppointments(patientId);
            return Result.success(appointments);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取医生的所有预约
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<Appointment>> getDoctorAppointments(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId);
            return Result.success(appointments);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取医生在特定日期的预约
     */
    @GetMapping("/doctor/{doctorId}/date")
    public Result<List<Appointment>> getDoctorAppointmentsForDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            List<Appointment> appointments = appointmentService.getDoctorAppointmentsForDate(doctorId, date);
            return Result.success(appointments);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 取消预约
     */
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelAppointment(@PathVariable Long id) {
        try {
            boolean success = appointmentService.cancelAppointment(id);
            return Result.success("预约已取消", success);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
