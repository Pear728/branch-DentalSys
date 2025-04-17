package com.dental.service;

/**
 * 数据迁移服务接口
 * 用于处理数据迁移相关的操作
 */
public interface DataMigrationService {
    
    /**
     * 迁移已有支付记录到Payment表
     * 将t_appointment表中的已支付记录迁移到t_payment表
     * @return 迁移的记录数
     */
    int migratePaymentData();
}
