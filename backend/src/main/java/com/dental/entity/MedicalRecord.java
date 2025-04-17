package com.dental.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 病历信息实体类
 */
@Data
@Entity
@Table(name = "t_medical_record")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 病历编号
     */
    @Column(name = "record_no", nullable = false, unique = true, length = 30)
    private String recordNo;

    /**
     * 关联的预约ID
     */
    @Column(name = "appointment_id")
    private Long appointmentId;

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
     * 就诊日期
     */
    @Column(name = "record_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date recordDate;

    /**
     * 主诉
     */
    @Column(name = "chief_complaint", columnDefinition = "text")
    private String chiefComplaint;

    /**
     * 诊断结果
     */
    @Column(name = "diagnosis", columnDefinition = "text")
    private String diagnosis;

    /**
     * 治疗方案
     */
    @Column(name = "treatment", columnDefinition = "text")
    private String treatment;

    /**
     * 处方
     */
    @Column(name = "prescription", columnDefinition = "text")
    private String prescription;

    /**
     * 下次就诊建议
     */
    @Column(name = "next_visit_suggestion", columnDefinition = "text")
    private String nextVisitSuggestion;
    
    /**
     * 牙齿状况记录
     */
    @Column(name = "teeth_condition", columnDefinition = "text")
    private String teethCondition;
    
    /**
     * 状态（1-有效，0-无效）
     */
    @Column(name = "status", columnDefinition = "tinyint(1) default 1")
    private Integer status = 1;
    
    /**
     * 备注
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 兼容字段，与数据库中的visit_date字段对应
     */
    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;
}
