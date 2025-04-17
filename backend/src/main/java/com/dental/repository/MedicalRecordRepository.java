package com.dental.repository;

import com.dental.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 病历数据访问接口
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    /**
     * 查询患者的所有病历
     */
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);
    
    /**
     * 查询医生创建的所有病历
     */
    List<MedicalRecord> findByDoctorIdOrderByRecordDateDesc(Long doctorId);
    
    /**
     * 根据预约ID查询病历
     */
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
    
    /**
     * 查询特定日期范围内的病历
     */
    List<MedicalRecord> findByRecordDateBetweenOrderByRecordDateDesc(Date startDate, Date endDate);
    
    /**
     * 根据病历编号查询病历
     */
    Optional<MedicalRecord> findByRecordNo(String recordNo);
}
