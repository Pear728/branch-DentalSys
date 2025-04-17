package com.dental.service.impl;

import com.dental.entity.PatientInfo;
import com.dental.repository.PatientInfoRepository;
import com.dental.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * 患者服务实现类
 */
@Service
public class PatientServiceImpl implements PatientService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    
    @Autowired
    private PatientInfoRepository patientInfoRepository;
    
    @Override
    public PatientInfo getPatientInfoByUserId(Long userId) {
        logger.info("获取患者信息：userId={}", userId);
        
        if (userId == null) {
            logger.error("获取患者信息失败：userId不能为空");
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return patientInfoRepository.findByUserId(userId)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public PatientInfo updatePatientInfo(PatientInfo patientInfo) {
        logger.info("更新患者信息：userId={}", patientInfo.getUserId());
        
        if (patientInfo == null || patientInfo.getUserId() == null) {
            logger.error("更新患者信息失败：患者信息或userId为空");
            throw new IllegalArgumentException("患者信息或用户ID不能为空");
        }
        
        // 检查患者信息是否存在
        Optional<PatientInfo> existingInfo = patientInfoRepository.findByUserId(patientInfo.getUserId());
        
        if (existingInfo.isPresent()) {
            PatientInfo existing = existingInfo.get();
            
            // 只更新提供的非空字段
            if (patientInfo.getBirthDate() != null) {
                existing.setBirthDate(patientInfo.getBirthDate());
            }
            if (patientInfo.getIdCard() != null) {
                existing.setIdCard(patientInfo.getIdCard());
            }
            if (patientInfo.getAddress() != null) {
                existing.setAddress(patientInfo.getAddress());
            }
            if (patientInfo.getEmergencyContact() != null) {
                existing.setEmergencyContact(patientInfo.getEmergencyContact());
            }
            if (patientInfo.getEmergencyPhone() != null) {
                existing.setEmergencyPhone(patientInfo.getEmergencyPhone());
            }
            if (patientInfo.getMedicalHistory() != null) {
                existing.setMedicalHistory(patientInfo.getMedicalHistory());
            }
            if (patientInfo.getAllergies() != null) {
                existing.setAllergies(patientInfo.getAllergies());
            }
            
            // 更新修改时间
            existing.setUpdateTime(new Date());
            
            logger.info("更新患者信息成功：userId={}", patientInfo.getUserId());
            return patientInfoRepository.save(existing);
        } else {
            // 如果不存在，则创建新记录
            logger.info("患者信息不存在，创建新记录：userId={}", patientInfo.getUserId());
            return createPatientInfo(patientInfo);
        }
    }
    
    @Override
    @Transactional
    public PatientInfo createPatientInfo(PatientInfo patientInfo) {
        logger.info("创建患者信息：userId={}", patientInfo.getUserId());
        
        if (patientInfo == null || patientInfo.getUserId() == null) {
            logger.error("创建患者信息失败：患者信息或userId为空");
            throw new IllegalArgumentException("患者信息或用户ID不能为空");
        }
        
        // 确保创建时间和更新时间不为空
        patientInfo.setCreateTime(new Date());
        patientInfo.setUpdateTime(new Date());
        
        return patientInfoRepository.save(patientInfo);
    }
    
    @Override
    @Transactional
    public boolean deletePatientInfo(Long userId) {
        logger.info("删除患者信息：userId={}", userId);
        
        if (userId == null) {
            logger.error("删除患者信息失败：userId为空");
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        Optional<PatientInfo> existingInfo = patientInfoRepository.findByUserId(userId);
        
        if (existingInfo.isPresent()) {
            patientInfoRepository.delete(existingInfo.get());
            logger.info("删除患者信息成功：userId={}", userId);
            return true;
        } else {
            logger.warn("删除患者信息失败：患者信息不存在, userId={}", userId);
            return false;
        }
    }
}
