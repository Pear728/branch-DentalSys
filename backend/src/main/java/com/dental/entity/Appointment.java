package com.dental.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 预约挂号记录实体类
 */
@Data
@Entity
@Table(name = "t_appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 预约编号
     */
    @Column(name = "appointment_no", nullable = false, unique = true)
    private String appointmentNo;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    /**
     * 医生ID
     */
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    /**
     * 排班ID
     */
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    /**
     * 预约日期
     */
    @Column(name = "appointment_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date appointmentDate;
    
    /**
     * 预约时间段
     */
    @Column(name = "appointment_time", nullable = false, length = 20)
    private String appointmentTime;

    /**
     * 预约状态：0-待确认，1-已确认，2-已完成，3-已取消
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    /**
     * 预约症状描述
     */
    @Column(name = "symptom_desc", length = 500)
    private String symptomDesc;
    
    /**
     * 就诊项目
     */
    @Column(name = "treatment_item", length = 100)
    private String treatmentItem;

    /**
     * 备注
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 订单金额
     */
    @Column(name = "order_amount", precision = 10, scale = 2)
    private Double orderAmount;

    /**
     * 支付状态：0-未支付，1-已支付
     */
    @Column(name = "is_paid")
    private Integer paymentStatus = 0;

    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTime;
    
    /**
     * 支付方式
     */
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;
    
    /**
     * 交易流水号
     */
    @Column(name = "transaction_id", length = 64)
    private String transactionId;
    
    /**
     * 取消原因
     */
    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;
}
