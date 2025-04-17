package com.dental.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 医生排班信息实体类
 */
@Data
@Entity
@Table(name = "t_schedule")
public class Schedule {
    
    // 使用显式的命名策略，确保Java属性名与数据库字段名一致

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 医生ID
     */
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    /**
     * 排班日期
     */
    @Column(name = "schedule_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date scheduleDate;

    /**
     * 开始时间（小时:分钟）
     */
    @Column(name = "start_time", nullable = false, length = 10)
    private String startTime;

    /**
     * 结束时间（小时:分钟）
     */
    @Column(name = "end_time", nullable = false, length = 10)
    private String endTime;

    /**
     * 可预约人数上限
     */
    @Column(name = "max_appointments", nullable = false)
    private Integer maxAppointments = 10;

    /**
     * 已预约人数
     */
    @Column(name = "appointed_count", nullable = false)
    private Integer appointedCount = 0; // 确保默认值为0

    /**
     * 状态：0-不可用，1-可预约
     */
    @Column(nullable = false)
    private Integer status = 1;

    /**
     * 备注
     */
    @Column(length = 200)
    private String remark;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;
}
