package com.dental.repository;

import com.dental.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付记录数据访问接口
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * 根据支付状态查询支付记录
     * @param paymentStatus 支付状态
     * @return 支付记录列表
     */
    List<Payment> findByPaymentStatus(int paymentStatus);
    
    /**
     * 根据用户ID查询支付记录
     * @param userId 用户ID
     * @return 支付记录列表
     */
    List<Payment> findByUserId(Long userId);
    
    /**
     * 根据预约ID查询支付记录
     * @param appointmentId 预约ID
     * @return 支付记录列表
     */
    List<Payment> findByAppointmentId(Long appointmentId);
    
    /**
     * 根据支付时间范围查询支付记录
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 支付记录列表
     */
    List<Payment> findByPaymentTimeBetweenAndPaymentStatus(Date startDate, Date endDate, int paymentStatus);
    
    /**
     * 查询指定时间段内的支付总额
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param paymentStatus 支付状态
     * @return 支付总额
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentTime BETWEEN :startDate AND :endDate AND p.paymentStatus = :paymentStatus")
    BigDecimal sumAmountByPaymentTimeBetweenAndPaymentStatus(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("paymentStatus") int paymentStatus);
    
    /**
     * 查询特定日期的支付记录数量
     * @param paymentDate 支付日期
     * @param paymentStatus 支付状态
     * @return 支付记录数量
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE DATE(p.paymentTime) = DATE(:paymentDate) AND p.paymentStatus = :paymentStatus")
    long countByPaymentDateAndStatus(@Param("paymentDate") Date paymentDate, @Param("paymentStatus") int paymentStatus);
}
