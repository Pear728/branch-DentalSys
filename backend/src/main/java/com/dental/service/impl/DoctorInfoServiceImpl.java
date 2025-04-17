package com.dental.service.impl;

import com.dental.entity.DoctorInfo;
import com.dental.repository.DoctorInfoRepository;
import com.dental.service.DoctorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 医生信息服务实现类
 */
@Service
public class DoctorInfoServiceImpl implements DoctorInfoService {

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Override
    public DoctorInfo saveDoctorInfo(DoctorInfo doctorInfo) {
        return doctorInfoRepository.save(doctorInfo);
    }

    @Override
    public DoctorInfo getDoctorInfoByUserId(Long userId) {
        return doctorInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("医生信息不存在"));
    }

    @Override
    public DoctorInfo updateDoctorInfo(DoctorInfo doctorInfo) {
        // 验证医生信息是否存在
        if (!doctorInfoRepository.existsByUserId(doctorInfo.getUserId())) {
            throw new RuntimeException("医生信息不存在");
        }
        
        return doctorInfoRepository.save(doctorInfo);
    }
}
