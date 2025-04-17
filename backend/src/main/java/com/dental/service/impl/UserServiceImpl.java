package com.dental.service.impl;

import com.dental.entity.DoctorInfo;
import com.dental.entity.User;
import com.dental.repository.DoctorInfoRepository;
import com.dental.repository.UserRepository;
import com.dental.service.UserService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Override
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 根据角色ID设置角色字符串
        Integer roleId = user.getRoleId();
        if (roleId != null) {
            switch (roleId) {
                case 1:
                    user.setRole("PATIENT");
                    break;
                case 2:
                    user.setRole("DOCTOR");
                    break;
                case 3:
                    user.setRole("ADMIN");
                    break;
                default:
                    user.setRole("PATIENT"); // 默认为患者
            }
        } else {
            user.setRole("PATIENT"); // 默认为患者
        }
        
        // 设置默认状态为启用
        user.setStatus(1);
        
        // 生成随机盐值
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        
        // 使用SHA-256加密密码
        SimpleHash hash = new SimpleHash("SHA-256", user.getPassword(), 
                                         ByteSource.Util.bytes(salt), 1024);
        
        // 设置加密后的密码和盐值
        user.setPassword(hash.toHex());
        user.setSalt(salt);
        
        // 保存用户信息到t_user表
        User savedUser = userRepository.save(user);
        
        // 如果是医生角色，同时创建医生信息
        if ("DOCTOR".equals(savedUser.getRole())) {
            try {
                // 创建医生信息实体
                DoctorInfo doctorInfo = new DoctorInfo();
                doctorInfo.setUserId(savedUser.getId());
                
                // 设置医生信息
                doctorInfo.setProfessionalTitle(user.getTitle()); // 职称
                doctorInfo.setSpecialty(user.getSpecialty()); // 专长
                doctorInfo.setIntroduction(user.getIntroduction()); // 简介
                
                // 默认设置科室为口腔科
                doctorInfo.setDepartment("口腔科");
                
                // 保存医生信息
                doctorInfoRepository.save(doctorInfo);
                
                System.out.println("医生信息保存成功，医生 ID: " + savedUser.getId());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("创建医生信息失败: " + e.getMessage());
            }
        }
        
        return savedUser;
    }

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        // 判断用户是否存在
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 判断用户状态是否正常
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 验证密码
        String salt = user.getSalt();
        SimpleHash hash = new SimpleHash("SHA-256", password, 
                                         ByteSource.Util.bytes(salt), 1024);
        
        if (!user.getPassword().equals(hash.toHex())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User updateUser(User user) {
        // 验证用户是否存在
        User existingUser = getUserById(user.getId());
        
        // 如果修改了用户名，需要检查新用户名是否已存在
        if (user.getUsername() != null && !existingUser.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 只更新提供的字段，保留其他字段
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        
        // 打印日志便于调试
        System.out.println("更新用户信息: " + existingUser.getId());
        System.out.println("真实姓名: " + existingUser.getRealName());
        System.out.println("性别: " + existingUser.getGender());
        System.out.println("手机号: " + existingUser.getPhone());
        System.out.println("邮箱: " + existingUser.getEmail());
        
        // 更新用户信息
        return userRepository.save(existingUser);
    }

    @Override
    public List<User> getDoctorList() {
        // 查询医生列表（角色为DOCTOR）
        return userRepository.findByRole("DOCTOR");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchUsers(String role, String realName) {
        // 查询所有用户
        List<User> users = userRepository.findAll();
        
        // 根据角色和姓名进行筛选
        return users.stream()
                .filter(user -> role == null || user.getRole().equals(role))
                .filter(user -> realName == null || 
                        user.getRealName().contains(realName))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        // 查询用户
        User user = getUserById(userId);
        
        // 更新状态
        user.setStatus(status);
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        User user = getUserById(userId);
        
        // 验证旧密码是否正确
        String salt = user.getSalt();
        SimpleHash oldHash = new SimpleHash("SHA-256", oldPassword, 
                                           ByteSource.Util.bytes(salt), 1024);
        
        if (!user.getPassword().equals(oldHash.toHex())) {
            throw new RuntimeException("原密码不正确");
        }
        
        // 生成新的加密密码
        SimpleHash newHash = new SimpleHash("SHA-256", newPassword, 
                                           ByteSource.Util.bytes(salt), 1024);
        
        // 更新密码
        user.setPassword(newHash.toHex());
        userRepository.save(user);
        
        return true;
    }
}
