package com.dental.service;

/**
 * 验证码服务接口
 * 处理验证码的生成、发送和验证
 */
public interface VerificationService {
    
    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param type 验证码类型（如：changePhone, changeEmail）
     * @return 生成的验证码（仅在开发环境返回，生产环境应返回null）
     */
    String sendSmsVerificationCode(String phone, String type);
    
    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     * @param type 验证码类型（如：changePhone, changeEmail）
     * @return 生成的验证码（仅在开发环境返回，生产环境应返回null）
     */
    String sendEmailVerificationCode(String email, String type);
    
    /**
     * 验证并更新手机号
     * @param userId 用户ID
     * @param phone 新手机号
     * @param code 验证码
     * @return 是否验证成功并更新
     */
    boolean verifyAndUpdatePhone(Long userId, String phone, String code);
    
    /**
     * 验证并更新邮箱
     * @param userId 用户ID
     * @param email 新邮箱
     * @param code 验证码
     * @return 是否验证成功并更新
     */
    boolean verifyAndUpdateEmail(Long userId, String email, String code);
}
