package com.dental.repository;

import com.dental.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 系统日志数据访问接口
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    
    /**
     * 根据条件分页查询日志
     * @param username 用户名（可选）
     * @param operation 操作类型（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param pageable 分页参数
     * @return 分页日志数据
     */
    @Query("SELECT l FROM SystemLog l WHERE " +
           "(:username IS NULL OR l.username LIKE CONCAT('%', :username, '%')) AND " +
           "(:operation IS NULL OR l.operation LIKE CONCAT('%', :operation, '%')) AND " +
           "(:startDate IS NULL OR l.createTime >= :startDate) AND " +
           "(:endDate IS NULL OR l.createTime <= :endDate) " +
           "ORDER BY l.createTime DESC")
    Page<SystemLog> findByConditions(
            @Param("username") String username,
            @Param("operation") String operation,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);
}
