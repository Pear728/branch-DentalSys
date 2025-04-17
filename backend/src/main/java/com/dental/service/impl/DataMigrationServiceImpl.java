package com.dental.service.impl;

import com.dental.entity.Appointment;
import com.dental.entity.Payment;
import com.dental.repository.AppointmentRepository;
import com.dental.repository.PaymentRepository;
import com.dental.service.DataMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 数据迁移服务实现类
 */
@Service
public class DataMigrationServiceImpl implements DataMigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataMigrationServiceImpl.class);
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Override
    @Transactional
    public int migratePaymentData() {
        logger.info("开始迁移支付数据...");
        
        // 查询所有状态为已支付的预约记录
        List<Appointment> paidAppointments = appointmentRepository.findByPaymentStatus(1);
        
        int count = 0;
        for (Appointment appointment : paidAppointments) {
            // 检查是否已经有对应的支付记录
            List<Payment> existingPayments = paymentRepository.findByAppointmentId(appointment.getId());
            if (!existingPayments.isEmpty()) {
                logger.info("预约ID: {} 已存在支付记录，跳过", appointment.getId());
                continue;
            }
            
            try {
                // 创建新的支付记录
                Payment payment = new Payment();
                
                // 生成支付流水号：格式为PM(Migration) + 年月日时分秒 + 4位随机数
                String dateStr = String.format("%tY%<tm%<td%<tH%<tM%<tS", new Date());
                String randomNum = String.format("%04d", (int)(Math.random() * 10000));
                String paymentNo = "PM" + dateStr + randomNum;
                
                payment.setPaymentNo(paymentNo);
                payment.setUserId(appointment.getPatientId());
                payment.setAppointmentId(appointment.getId());
                
                // 设置金额
                if (appointment.getOrderAmount() != null) {
                    payment.setAmount(new BigDecimal(appointment.getOrderAmount()));
                } else {
                    // 默认金额为100
                    payment.setAmount(new BigDecimal("100.00"));
                }
                
                // 设置支付方式，如果为空则设为"online"
                payment.setPaymentMethod(appointment.getPaymentMethod() != null ? 
                        appointment.getPaymentMethod() : "online");
                
                // 设置支付状态为已支付
                payment.setPaymentStatus(1);
                
                // 设置支付时间
                payment.setPaymentTime(appointment.getPaymentTime() != null ? 
                        appointment.getPaymentTime() : new Date());
                
                // 设置支付类型
                payment.setPaymentType("挂号费");
                
                // 设置备注
                payment.setRemark("预约"+ 
                        (appointment.getTreatmentItem() != null ? appointment.getTreatmentItem() : "就诊") +
                        "缴费（数据迁移）");
                
                // 保存支付记录
                paymentRepository.save(payment);
                count++;
                
                logger.info("成功迁移预约ID: {} 的支付记录", appointment.getId());
            } catch (Exception e) {
                logger.error("迁移预约ID: {} 的支付记录失败: {}", appointment.getId(), e.getMessage());
            }
        }
        
        logger.info("支付数据迁移完成，共迁移 {} 条记录", count);
        return count;
    }
}
