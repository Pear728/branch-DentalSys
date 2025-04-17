package com.dental.service.impl;

import com.dental.entity.SystemLog;
import com.dental.repository.SystemLogRepository;
import com.dental.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;

/**
 * 系统日志服务实现类
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private SystemLogRepository systemLogRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SystemLog saveLog(Long userId, String username, String operation, String method, 
                           String params, String ip, Integer status, String errorMsg, Long operationTime) {
        SystemLog log = new SystemLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        log.setStatus(status);
        log.setErrorMsg(errorMsg);
        log.setOperationTime(operationTime);
        
        return systemLogRepository.save(log);
    }

    @Override
    @Transactional
    public int deleteLogsBefore(int days) {
        // 计算指定天数前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date date = calendar.getTime();
        
        // 使用原生SQL执行批量删除，提高效率
        String sql = "DELETE FROM t_system_log WHERE create_time < :date";
        int deletedCount = entityManager.createNativeQuery(sql)
                .setParameter("date", date)
                .executeUpdate();
        
        return deletedCount;
    }
}
