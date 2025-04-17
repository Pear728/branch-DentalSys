package com.dental.service.impl;

import com.dental.entity.User;
import com.dental.service.UserService;
import com.dental.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务实现类
 * 注意：这是一个开发环境的模拟实现，不实际发送短信或邮件
 * 生产环境应替换为实际的短信和邮件服务
 */
@Service
public class VerificationServiceImpl implements VerificationService {

    // 验证码有效期（毫秒）
    private static final long CODE_EXPIRATION = 5 * 60 * 1000; // 5分钟
    
    // 存储验证码的Map: key=类型_接收者, value=验证码_过期时间
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    
    @Autowired
    private UserService userService;
    
    @Override
    public String sendSmsVerificationCode(String phone, String type) {
        // 生成6位数字验证码
        String code = generateNumericCode(6);
        
        // 在实际应用中，这里应该调用短信发送服务
        // 例如: smsService.send(phone, "您的验证码是：" + code + "，5分钟内有效。");
        
        // 计算过期时间
        long expirationTime = System.currentTimeMillis() + CODE_EXPIRATION;
        
        // 存储验证码和过期时间
        String key = type + "_" + phone;
        String value = code + "_" + expirationTime;
        verificationCodes.put(key, value);
        
        System.out.println("[开发模式] 向手机 " + phone + " 发送验证码: " + code);
        
        // 返回验证码（仅在开发环境）
        return code;
    }

    @Override
    public String sendEmailVerificationCode(String email, String type) {
        // 生成6位数字+字母验证码
        String code = generateAlphanumericCode(6);
        
        // 在实际应用中，这里应该调用邮件发送服务
        // 例如: emailService.send(email, "验证码", "您的验证码是：" + code + "，5分钟内有效。");
        
        // 计算过期时间
        long expirationTime = System.currentTimeMillis() + CODE_EXPIRATION;
        
        // 存储验证码和过期时间
        String key = type + "_" + email;
        String value = code + "_" + expirationTime;
        verificationCodes.put(key, value);
        
        System.out.println("[开发模式] 向邮箱 " + email + " 发送验证码: " + code);
        
        // 返回验证码（仅在开发环境）
        return code;
    }

    @Override
    public boolean verifyAndUpdatePhone(Long userId, String phone, String code) {
        // 验证码校验
        String key = "changePhone_" + phone;
        if (!verifyCode(key, code)) {
            return false;
        }
        
        try {
            // 获取用户
            User user = userService.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 更新手机号
            user.setPhone(phone);
            userService.updateUser(user);
            
            // 清除验证码
            verificationCodes.remove(key);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean verifyAndUpdateEmail(Long userId, String email, String code) {
        // 验证码校验
        String key = "changeEmail_" + email;
        if (!verifyCode(key, code)) {
            return false;
        }
        
        try {
            // 获取用户
            User user = userService.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 更新邮箱
            user.setEmail(email);
            userService.updateUser(user);
            
            // 清除验证码
            verificationCodes.remove(key);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证码校验
     * @param key 键值
     * @param code 用户提供的验证码
     * @return 是否验证通过
     */
    private boolean verifyCode(String key, String code) {
        String value = verificationCodes.get(key);
        if (value == null) {
            return false;
        }
        
        String[] parts = value.split("_");
        String storedCode = parts[0];
        long expirationTime = Long.parseLong(parts[1]);
        
        // 检查验证码是否过期
        if (System.currentTimeMillis() > expirationTime) {
            verificationCodes.remove(key);
            return false;
        }
        
        // 验证码匹配
        return code.equals(storedCode);
    }
    
    /**
     * 生成指定长度的数字验证码
     * @param length 验证码长度
     * @return 数字验证码
     */
    private String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 生成指定长度的数字+字母验证码
     * @param length 验证码长度
     * @return 数字+字母验证码
     */
    private String generateAlphanumericCode(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
