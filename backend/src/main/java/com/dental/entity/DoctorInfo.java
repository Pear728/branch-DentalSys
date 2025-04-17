package com.dental.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 医生信息实体类
 */
@Data
@Entity
@Table(name = "t_doctor_info")
public class DoctorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID，关联t_user表
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 科室
     */
    @Column(length = 50)
    private String department;

    /**
     * 职称
     */
    @Column(name = "professional_title", length = 50)
    private String professionalTitle;

    /**
     * 专长
     */
    @Column(length = 255)
    private String specialty;

    /**
     * 简介
     */
    @Column(columnDefinition = "text")
    private String introduction;

    /**
     * 执业证号
     */
    @Column(name = "certificate_no", length = 50)
    private String certificateNo;

    /**
     * 从业年限
     */
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience = 0;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;
}
