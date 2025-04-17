package com.dental.controller;

import com.dental.common.Result;
import com.dental.entity.User;
import com.dental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            // 将性别字符串转换为对应的整数值：男->0，女->1
            Object genderObj = user.getGender();
            if (genderObj != null && genderObj instanceof String) {
                String genderStr = (String) genderObj;
                if ("男".equals(genderStr)) {
                    user.setGender(0);
                } else if ("女".equals(genderStr)) {
                    user.setGender(1);
                }
            }
            
            // 打印注册信息，便于调试
            System.out.println("Registering user: " + user.getUsername());
            System.out.println("Role ID: " + user.getRoleId());
            System.out.println("Title: " + user.getTitle());
            System.out.println("Specialty: " + user.getSpecialty());
            
            User registeredUser = userService.register(user);
            return Result.success("注册成功", registeredUser);
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息便于调试
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        try {
            User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
            return Result.success("登录成功", user);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping
    public Result<User> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return Result.success("更新成功", updatedUser);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取医生列表
     */
    @GetMapping("/doctors")
    public Result<List<User>> getDoctorList() {
        try {
            List<User> doctors = userService.getDoctorList();
            return Result.success(doctors);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取所有用户
     */
    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam(required = false) String role,
                                        @RequestParam(required = false) String realName) {
        try {
            List<User> users = userService.searchUsers(role, realName);
            return Result.success(users);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 修改用户状态
     */
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @PathVariable int status) {
        try {
            boolean success = userService.updateUserStatus(id, status);
            return Result.success("状态更新成功", success);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/{username}")
    public Result<Boolean> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.checkUsernameExists(username);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
