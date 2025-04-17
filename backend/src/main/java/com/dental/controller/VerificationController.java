package com.dental.controller;

import com.dental.common.Result;
import com.dental.dto.VerificationRequest;
import com.dental.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器
 * 处理发送验证码和验证相关的请求
 */
@RestController
@RequestMapping("/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;
    
    /**
     * 发送手机验证码
     * @param request 包含手机号和验证码类型
     * @return 发送结果
     */
    @PostMapping("/send-sms")
    public Result<String> sendSmsVerificationCode(@RequestBody VerificationRequest request) {
        try {
            String phone = request.getPhone();
            String type = request.getType();
            
            if (phone == null || phone.isEmpty()) {
                return Result.fail("手机号不能为空");
            }
            
            // 验证手机号格式
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return Result.fail("手机号格式不正确");
            }
            
            String code = verificationService.sendSmsVerificationCode(phone, type);
            return Result.success("验证码发送成功", code);
        } catch (Exception e) {
            return Result.fail("发送验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送邮箱验证码
     * @param request 包含邮箱和验证码类型
     * @return 发送结果
     */
    @PostMapping("/send-email")
    public Result<String> sendEmailVerificationCode(@RequestBody VerificationRequest request) {
        try {
            String email = request.getEmail();
            String type = request.getType();
            
            if (email == null || email.isEmpty()) {
                return Result.fail("邮箱不能为空");
            }
            
            // 验证邮箱格式
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                return Result.fail("邮箱格式不正确");
            }
            
            String code = verificationService.sendEmailVerificationCode(email, type);
            return Result.success("验证码发送成功", code);
        } catch (Exception e) {
            return Result.fail("发送验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证并更新手机号
     * @param request 包含用户ID、新手机号和验证码
     * @return 更新结果
     */
    @PutMapping("/verify-phone")
    public Result<Boolean> verifyAndUpdatePhone(@RequestBody VerificationRequest request) {
        try {
            Long userId = request.getUserId();
            String phone = request.getPhone();
            String code = request.getCode();
            
            if (userId == null) {
                return Result.fail("用户ID不能为空");
            }
            
            if (phone == null || phone.isEmpty()) {
                return Result.fail("手机号不能为空");
            }
            
            if (code == null || code.isEmpty()) {
                return Result.fail("验证码不能为空");
            }
            
            boolean success = verificationService.verifyAndUpdatePhone(userId, phone, code);
            return success ? Result.success("手机号更新成功", true) : Result.fail("验证码错误或已过期");
        } catch (Exception e) {
            return Result.fail("更新手机号失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证并更新邮箱
     * @param request 包含用户ID、新邮箱和验证码
     * @return 更新结果
     */
    @PutMapping("/verify-email")
    public Result<Boolean> verifyAndUpdateEmail(@RequestBody VerificationRequest request) {
        try {
            Long userId = request.getUserId();
            String email = request.getEmail();
            String code = request.getCode();
            
            if (userId == null) {
                return Result.fail("用户ID不能为空");
            }
            
            if (email == null || email.isEmpty()) {
                return Result.fail("邮箱不能为空");
            }
            
            if (code == null || code.isEmpty()) {
                return Result.fail("验证码不能为空");
            }
            
            boolean success = verificationService.verifyAndUpdateEmail(userId, email, code);
            return success ? Result.success("邮箱更新成功", true) : Result.fail("验证码错误或已过期");
        } catch (Exception e) {
            return Result.fail("更新邮箱失败: " + e.getMessage());
        }
    }
}
