package com.dental.service;

import com.dental.entity.PatientInfo;

/**
 * 患者服务接口
 */
public interface PatientService {
    
    /**
     * 通过用户ID获取患者信息
     * 
     * @param userId 用户ID
     * @return 患者信息
     */
    PatientInfo getPatientInfoByUserId(Long userId);
    
    /**
     * 更新患者信息
     * 
     * @param patientInfo 患者信息
     * @return 更新后的患者信息
     */
    PatientInfo updatePatientInfo(PatientInfo patientInfo);
    
    /**
     * 创建患者信息
     * 
     * @param patientInfo 患者信息
     * @return 创建的患者信息
     */
    PatientInfo createPatientInfo(PatientInfo patientInfo);
    
    /**
     * 删除患者信息
     * 
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deletePatientInfo(Long userId);
}
