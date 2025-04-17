package com.dental.service;

import com.dental.entity.DoctorInfo;

/**
 * 医生信息服务接口
 */
public interface DoctorInfoService {
    
    /**
     * 保存医生信息
     * @param doctorInfo 医生信息
     * @return 保存后的医生信息
     */
    DoctorInfo saveDoctorInfo(DoctorInfo doctorInfo);
    
    /**
     * 根据用户ID查找医生信息
     * @param userId 用户ID
     * @return 医生信息
     */
    DoctorInfo getDoctorInfoByUserId(Long userId);
    
    /**
     * 更新医生信息
     * @param doctorInfo 医生信息
     * @return 更新后的医生信息
     */
    DoctorInfo updateDoctorInfo(DoctorInfo doctorInfo);
}
