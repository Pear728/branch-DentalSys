package com.dental.service.impl;

import com.dental.entity.Appointment;
import com.dental.entity.Payment;
import com.dental.entity.Schedule;
import com.dental.repository.AppointmentRepository;
import com.dental.repository.PaymentRepository;
import com.dental.repository.ScheduleRepository;
import com.dental.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 预约服务实现类
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        // 获取预约日期
        Date appointmentDate = appointment.getAppointmentDate();
        
        // 查询医生在该日期的排班情况
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointmentDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date scheduleDate = calendar.getTime();
        
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndScheduleDate(
                appointment.getDoctorId(), scheduleDate);
        
        // 检查医生是否有排班
        if (schedules.isEmpty()) {
            throw new RuntimeException("该医生在所选日期没有出诊安排");
        }
        
        Schedule schedule = schedules.get(0);
        
        // 检查可预约人数是否已满
        if (schedule.getAppointedCount() >= schedule.getMaxAppointments()) {
            throw new RuntimeException("该时段预约人数已满");
        }
        
        // 更新排班已预约人数
        schedule.setAppointedCount(schedule.getAppointedCount() + 1);
        scheduleRepository.save(schedule);
        
        // 设置预约状态为待确认
        appointment.setStatus(0);
        
        // 设置排班ID
        appointment.setScheduleId(schedule.getId());
        
        // 生成唯一的预约编号（格式：AP + 年月日 + 6位随机数）
        String dateStr = String.format("%tY%<tm%<td", new Date());
        String randomNum = String.format("%06d", (int)(Math.random() * 1000000));
        String appointmentNo = "AP" + dateStr + randomNum;
        appointment.setAppointmentNo(appointmentNo);
        
        // 保存预约信息
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
    }

    @Override
    public Appointment updateAppointmentStatus(Long id, Integer status) {
        // 获取预约信息
        Appointment appointment = getAppointmentById(id);
        
        // 更新状态
        appointment.setStatus(status);
        
        return appointmentRepository.save(appointment);
    }

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Override
    @Transactional
    public Appointment payForAppointment(Long id, Double amount) {
        // 调用带支付方式的方法，默认使用online支付
        return payForAppointment(id, amount, "online");
    }
    
    @Override
    @Transactional
    public Appointment payForAppointment(Long id, Double amount, String paymentMethod) {
        // 获取预约信息
        Appointment appointment = getAppointmentById(id);
        
        // 更新支付状态和金额
        appointment.setPaymentStatus(1);
        appointment.setOrderAmount(amount);
        appointment.setPaymentMethod(paymentMethod);
        Date paymentTime = new Date();
        appointment.setPaymentTime(paymentTime);
        
        // 创建支付记录
        Payment payment = new Payment();
        
        // 生成支付流水号：格式为P + 年月日时分秒 + 4位随机数
        String dateStr = String.format("%tY%<tm%<td%<tH%<tM%<tS", paymentTime);
        String randomNum = String.format("%04d", (int)(Math.random() * 10000));
        String paymentNo = "P" + dateStr + randomNum;
        
        payment.setPaymentNo(paymentNo);
        payment.setUserId(appointment.getPatientId());
        payment.setAppointmentId(appointment.getId());
        payment.setAmount(new BigDecimal(amount));
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(1); // 已支付
        payment.setPaymentTime(paymentTime);
        payment.setPaymentType("挂号费");
        payment.setRemark("预约"+ appointment.getTreatmentItem() +"缴费");
        
        // 保存支付记录
        paymentRepository.save(payment);
        
        // 返回更新后的预约信息
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
    }

    @Override
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }

    @Override
    public List<Appointment> getDoctorAppointmentsForDate(Long doctorId, Date date) {
        // 设置日期范围（当天）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date appointmentDate = calendar.getTime();
        
        return appointmentRepository.findByDoctorIdAndAppointmentDate(
                doctorId, appointmentDate);
    }

    @Override
    @Transactional
    public boolean cancelAppointment(Long id) {
        // 获取预约信息
        Appointment appointment = getAppointmentById(id);
        
        // 只有待确认和已确认状态的预约可以取消
        if (appointment.getStatus() != 0 && appointment.getStatus() != 1) {
            throw new RuntimeException("当前预约状态不可取消");
        }
        
        // 更新预约状态为已取消
        appointment.setStatus(3);
        appointmentRepository.save(appointment);
        
        // 获取预约日期
        Date appointmentDate = appointment.getAppointmentDate();
        
        // 查询医生在该日期的排班情况
        Date scheduleDate = appointmentDate; // 直接使用预约日期，因为已经是DATE类型
        
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndScheduleDate(
                appointment.getDoctorId(), scheduleDate);
        
        // 更新排班已预约人数
        if (!schedules.isEmpty()) {
            Schedule schedule = schedules.get(0);
            schedule.setAppointedCount(Math.max(0, schedule.getAppointedCount() - 1));
            scheduleRepository.save(schedule);
        }
        
        return true;
    }
}
