package com.dental.service;

import com.dental.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(User user);
    
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 更新用户信息
     */
    User updateUser(User user);
    
    /**
     * 查询医生列表
     */
    List<User> getDoctorList();
    
    /**
     * 查询所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据条件查询用户
     */
    List<User> searchUsers(String role, String realName);
    
    /**
     * 修改用户状态
     */
    boolean updateUserStatus(Long userId, Integer status);
    
    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);
    
    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
