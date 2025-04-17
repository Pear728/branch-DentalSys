package com.dental.controller;

import com.dental.common.Result;
import com.dental.entity.PatientInfo;
import com.dental.entity.User;
import com.dental.service.PatientService;
import com.dental.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 患者控制器
 */
@RestController
@RequestMapping("/patient")
public class PatientController {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private PatientService patientService;
    
    /**
     * 测试API是否可访问的简单接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("PatientController is working!");
    }

    /**
     * 获取患者个人资料
     */
    @GetMapping("/profile/{id}")
    public Result<User> getProfile(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            // 处理敏感信息，比如密码不应该返回给前端
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.fail("用户不存在");
    }

    /**
     * 更新患者个人资料
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody PatientInfo patientInfo) {
        logger.info("接收到患者资料更新请求: userId={}", patientInfo.getUserId());
        
        try {
            PatientInfo updatedInfo = patientService.updatePatientInfo(patientInfo);
            if (updatedInfo != null) {
                return Result.success("患者资料更新成功");
            } else {
                logger.error("患者资料更新失败，患者ID: {}", patientInfo.getUserId());
                return Result.fail("患者资料更新失败");
            }
        } catch (Exception e) {
            logger.error("患者资料更新异常", e);
            return Result.fail("患者资料更新异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取患者详细资料
     */
    @GetMapping("/detail/{userId}")
    public Result<PatientInfo> getPatientDetail(@PathVariable Long userId) {
        logger.info("获取患者详细资料: userId={}", userId);
        
        try {
            PatientInfo patientInfo = patientService.getPatientInfoByUserId(userId);
            if (patientInfo != null) {
                return Result.success(patientInfo);
            } else {
                return Result.fail("未找到患者资料");
            }
        } catch (Exception e) {
            logger.error("获取患者详细资料异常", e);
            return Result.fail("获取患者资料异常: " + e.getMessage());
        }
    }

    /**
     * 修改患者密码
     */
    @PutMapping("/change-password")
    public Result<String> changePassword(@RequestParam Long userId, 
                                         @RequestParam String oldPassword, 
                                         @RequestParam String newPassword) {
        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (success) {
            return Result.success("密码修改成功");
        }
        return Result.fail("密码修改失败，原密码错误或者其他错误");
    }
}
