package com.dental.controller;

import com.dental.common.Result;
import com.dental.entity.User;
import com.dental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 医生控制器
 */
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private UserService userService;
    
    /**
     * 测试API是否可访问的简单接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("DoctorController is working!");
    }

    /**
     * 获取医生个人资料
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
     * 更新医生个人资料
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user) {
        // 添加日志，便于调试
        System.out.println("更新医生个人资料, 用户ID: " + user.getId());
        
        // 确保用户ID不为空
        if (user.getId() == null) {
            return Result.fail("用户ID不能为空");
        }
        
        try {
            User updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                return Result.success("个人资料更新成功");
            }
            return Result.fail("个人资料更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("个人资料更新异常: " + e.getMessage());
        }
    }

    /**
     * 修改医生密码
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
