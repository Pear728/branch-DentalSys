package com.dental.service.impl;

import com.dental.entity.Appointment;
import com.dental.entity.MedicalRecord;
import com.dental.repository.AppointmentRepository;
import com.dental.repository.MedicalRecordRepository;
import com.dental.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 病历服务实现类
 */
@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        try {
            // 验证预约记录是否存在
            Long appointmentId = medicalRecord.getAppointmentId();
            if (appointmentId != null) {
                Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
                if (!appointmentOpt.isPresent()) {
                    throw new RuntimeException("预约记录不存在");
                }
                
                // 检查是否已存在相关病历
                Optional<MedicalRecord> existingRecord = medicalRecordRepository.findByAppointmentId(appointmentId);
                if (existingRecord.isPresent()) {
                    throw new RuntimeException("该预约已创建病历，不能重复创建");
                }
                
                // 更新预约状态为已完成
                Appointment appointment = appointmentOpt.get();
                appointment.setStatus(2);
                appointmentRepository.save(appointment);
            }
            
            // 如果未设置就诊日期，则使用当前时间
            Date currentDate = new Date();
            if (medicalRecord.getRecordDate() == null) {
                medicalRecord.setRecordDate(currentDate);
            }
            
            // 同时设置visitDate字段，与recordDate保持一致
            medicalRecord.setVisitDate(medicalRecord.getRecordDate());
            
            // 生成病历编号（格式：MR + 年月日 + 6位随机数）
            String dateStr = String.format("%tY%<tm%<td", new Date());
            String randomNum = String.format("%06d", (int)(Math.random() * 1000000));
            String recordNo = "MR" + dateStr + randomNum;
            
            // 设置病历编号
            medicalRecord.setRecordNo(recordNo);
            
            // 将treatmentPlan字段的值设置到treatment字段
            if (medicalRecord.getTreatment() == null) {
                medicalRecord.setTreatment("");
            }
            
            // 将followUpPlan字段的值设置到nextVisitSuggestion字段
            if (medicalRecord.getNextVisitSuggestion() == null) {
                medicalRecord.setNextVisitSuggestion("");
            }
            
            // 设置默认状态为有效
            if (medicalRecord.getStatus() == null) {
                medicalRecord.setStatus(1);
            }
            
            System.out.println("创建病历，病历编号: " + recordNo);
            System.out.println("病历数据: patientId=" + medicalRecord.getPatientId() + ", doctorId=" + medicalRecord.getDoctorId());
            
            // 保存病历信息
            return medicalRecordRepository.save(medicalRecord);
        } catch (Exception e) {
            System.err.println("创建病历失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("病历记录不存在"));
    }

    @Override
    public MedicalRecord getMedicalRecordByAppointmentId(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("该预约尚未创建病历"));
    }

    @Override
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        // 验证病历记录是否存在
        MedicalRecord existingRecord = getMedicalRecordById(medicalRecord.getId());
        
        // 更新病历信息
        return medicalRecordRepository.save(medicalRecord);
    }

    @Override
    public List<MedicalRecord> getPatientMedicalRecords(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
    }

    @Override
    public List<MedicalRecord> getDoctorMedicalRecords(Long doctorId) {
        return medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(doctorId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByDateRange(Date startDate, Date endDate) {
        return medicalRecordRepository.findByRecordDateBetweenOrderByRecordDateDesc(startDate, endDate);
    }
}
