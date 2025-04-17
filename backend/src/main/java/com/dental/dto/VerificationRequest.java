package com.dental.dto;

/**
 * 验证码请求DTO
 * 用于接收前端验证码相关请求的数据
 */
public class VerificationRequest {
    // 用户ID
    private Long userId;
    
    // 手机号
    private String phone;
    
    // 邮箱
    private String email;
    
    // 验证码
    private String code;
    
    // 验证码类型，如: changePhone, changeEmail等
    private String type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
