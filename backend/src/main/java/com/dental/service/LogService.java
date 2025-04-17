package com.dental.service;

import com.dental.entity.SystemLog;

/**
 * 系统日志服务接口
 */
public interface LogService {
    
    /**
     * 保存系统操作日志
     * @param userId 用户ID
     * @param username 用户名
     * @param operation 操作类型
     * @param method 方法名
     * @param params 参数信息
     * @param ip IP地址
     * @param status 操作状态(0:失败,1:成功)
     * @param errorMsg 错误信息(如果有)
     * @param operationTime 操作耗时(毫秒)
     * @return 保存的日志对象
     */
    SystemLog saveLog(Long userId, String username, String operation, String method, 
                     String params, String ip, Integer status, String errorMsg, Long operationTime);
    
    /**
     * 删除指定天数之前的日志
     * @param days 天数
     * @return 删除的记录数
     */
    int deleteLogsBefore(int days);
}
