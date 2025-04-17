package com.dental.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类，包含所有类型用户（患者、医生、管理员）
 */
@Data
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 128)
    private String password;
    
    /**
     * 密码盐值，用于加密
     */
    @Column(length = 50)
    private String salt;

    @Column(name = "real_name", nullable = false, length = 20)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    /**
     * 性别：0-男，1-女
     */
    @Column
    private Integer gender;

    @Column
    private Integer age;

    /**
     * 用户角色：PATIENT-患者，DOCTOR-医生，ADMIN-管理员
     */
    @Column(nullable = false)
    private String role;
    
    /**
     * 角色ID：1-患者，2-医生，3-管理员
     * 用于接收前端传来的角色ID
     */
    @Transient
    private Integer roleId;

    /**
     * 医生专业（仅角色为医生时有效）
     */
    @Column(length = 50)
    private String specialty;

    /**
     * 医生职称（仅角色为医生时有效）
     */
    @Column(length = 30)
    private String title;

    /**
     * 医生简介（仅角色为医生时有效）
     */
    @Column(length = 500)
    private String introduction;

    /**
     * 账号状态：0-禁用，1-启用
     */
    @Column(nullable = false)
    private Integer status = 1;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;
}
