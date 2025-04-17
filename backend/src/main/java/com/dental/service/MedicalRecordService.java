package com.dental.service;

import com.dental.entity.MedicalRecord;

import java.util.Date;
import java.util.List;

/**
 * 病历服务接口
 */
public interface MedicalRecordService {
    
    /**
     * 创建病历
     */
    MedicalRecord createMedicalRecord(MedicalRecord medicalRecord);
    
    /**
     * 根据ID查询病历
     */
    MedicalRecord getMedicalRecordById(Long id);
    
    /**
     * 根据预约ID查询病历
     */
    MedicalRecord getMedicalRecordByAppointmentId(Long appointmentId);
    
    /**
     * 更新病历信息
     */
    MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord);
    
    /**
     * 查询患者的所有病历
     */
    List<MedicalRecord> getPatientMedicalRecords(Long patientId);
    
    /**
     * 查询医生创建的所有病历
     */
    List<MedicalRecord> getDoctorMedicalRecords(Long doctorId);
    
    /**
     * 查询指定日期范围内的病历
     */
    List<MedicalRecord> getMedicalRecordsByDateRange(Date startDate, Date endDate);
}
