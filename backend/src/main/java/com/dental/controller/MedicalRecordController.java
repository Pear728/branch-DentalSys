package com.dental.controller;

import com.dental.common.Result;
import com.dental.entity.MedicalRecord;
import com.dental.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 病历控制器
 */
@RestController
@RequestMapping("/medical-record")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;
    
    /**
     * 创建病历
     */
    @PostMapping
    public Result<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord createdRecord = medicalRecordService.createMedicalRecord(medicalRecord);
            return Result.success("病历创建成功", createdRecord);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取病历详情
     */
    @GetMapping("/{id}")
    public Result<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        try {
            MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);
            return Result.success(medicalRecord);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 根据预约ID获取病历
     */
    @GetMapping("/appointment/{appointmentId}")
    public Result<MedicalRecord> getMedicalRecordByAppointmentId(@PathVariable Long appointmentId) {
        try {
            MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByAppointmentId(appointmentId);
            return Result.success(medicalRecord);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 更新病历信息
     */
    @PutMapping
    public Result<MedicalRecord> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecord(medicalRecord);
            return Result.success("病历更新成功", updatedRecord);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取患者的所有病历
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<MedicalRecord>> getPatientMedicalRecords(@PathVariable Long patientId) {
        try {
            List<MedicalRecord> records = medicalRecordService.getPatientMedicalRecords(patientId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取医生创建的所有病历
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalRecord>> getDoctorMedicalRecords(@PathVariable Long doctorId) {
        try {
            List<MedicalRecord> records = medicalRecordService.getDoctorMedicalRecords(doctorId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    
    /**
     * 获取指定日期范围内的病历
     */
    @GetMapping("/date-range")
    public Result<List<MedicalRecord>> getMedicalRecordsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDateRange(startDate, endDate);
            return Result.success(records);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
