package com.dental.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 患者信息实体类
 */
@Data
@Entity
@Table(name = "t_patient_info")
public class PatientInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID，关联t_user表
     */
    @Column(name = "user_id", unique = true)
    private Long userId;
    
    /**
     * 出生日期
     */
    @Column(name = "birth_date")
    private String birthDate;
    
    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String idCard;
    
    /**
     * 住址
     */
    private String address;
    
    /**
     * 紧急联系人
     */
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    /**
     * 紧急联系人电话
     */
    @Column(name = "emergency_phone")
    private String emergencyPhone;
    
    /**
     * 病史
     */
    @Column(name = "medical_history", length = 2000)
    private String medicalHistory;
    
    /**
     * 过敏史
     */
    @Column(name = "allergies", length = 2000)
    private String allergies;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
    
    /**
     * 创建时间
     */
    @PrePersist
    public void prePersist() {
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    /**
     * 更新时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = new Date();
    }
}
