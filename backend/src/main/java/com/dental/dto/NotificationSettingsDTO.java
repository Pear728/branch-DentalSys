package com.dental.dto;

import lombok.Data;

/**
 * 通知设置数据传输对象
 */
@Data
public class NotificationSettingsDTO {
    
    /**
     * 是否启用短信通知
     */
    private Boolean smsEnabled;
    
    /**
     * 短信通知类型，多个类型用逗号分隔
     */
    private String smsNotificationTypes;
    
    /**
     * 提醒时间（分钟）
     */
    private Integer reminderTime;
    
    /**
     * 是否启用邮件通知
     */
    private Boolean emailEnabled;
    
    /**
     * 邮件通知类型，多个类型用逗号分隔
     */
    private String emailNotificationTypes;
}
