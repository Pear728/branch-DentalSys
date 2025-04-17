package com.dental.repository;

import com.dental.entity.PatientInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 患者信息数据访问接口
 */
@Repository
public interface PatientInfoRepository extends JpaRepository<PatientInfo, Long> {
    
    /**
     * 通过用户ID查询患者信息
     * 
     * @param userId 用户ID
     * @return 患者信息
     */
    Optional<PatientInfo> findByUserId(Long userId);
    
    /**
     * 检查指定用户ID的患者信息是否存在
     * 
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(Long userId);
}
