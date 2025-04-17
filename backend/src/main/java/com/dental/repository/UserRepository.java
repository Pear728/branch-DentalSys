package com.dental.repository;

import com.dental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据用户名和密码查询用户
     */
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    /**
     * 根据角色查询用户列表
     */
    List<User> findByRole(String role);
    
    /**
     * 根据真实姓名模糊查询
     */
    List<User> findByRealNameContaining(String realName);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 统计指定角色的用户数量
     * @param role 角色
     * @return 用户数量
     */
    long countByRole(String role);
    
    /**
     * 统计指定时间之后创建的用户数量
     * @param date 时间点
     * @return 用户数量
     */
    long countByCreateTimeAfter(Date date);
}
