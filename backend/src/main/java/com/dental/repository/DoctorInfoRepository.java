package com.dental.repository;

import com.dental.entity.DoctorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 医生信息数据访问接口
 */
@Repository
public interface DoctorInfoRepository extends JpaRepository<DoctorInfo, Long> {
    
    /**
     * 根据用户ID查找医生信息
     * @param userId 用户ID
     * @return 医生信息
     */
    Optional<DoctorInfo> findByUserId(Long userId);
    
    /**
     * 检查用户ID是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(Long userId);
}
