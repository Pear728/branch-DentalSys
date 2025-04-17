package com.dental.controller;

import com.dental.common.Result;
import com.dental.service.DataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据迁移控制器
 */
@RestController
@RequestMapping("/system/migration")
public class DataMigrationController {
    
    @Autowired
    private DataMigrationService dataMigrationService;
    
    /**
     * 迁移支付数据到Payment表
     */
    @PostMapping("/payment")
    public Result<Integer> migratePaymentData() {
        try {
            int count = dataMigrationService.migratePaymentData();
            return Result.success("支付数据迁移成功", count);
        } catch (Exception e) {
            return Result.fail("支付数据迁移失败: " + e.getMessage());
        }
    }
}
