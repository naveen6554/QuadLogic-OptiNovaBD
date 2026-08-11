package com.optinova.repository;

import com.optinova.entity.Order;
import com.optinova.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface for Order entity data retrieval.
 * Provides custom query methods for user orders, status pagination, and revenue calculation.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<Order> findByOrderIdAndUserUserId(String orderId, Integer userId);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH p.images img " +
            "WHERE o.user.userId = :userId AND (o.status = :status OR o.status = com.optinova.entity.enums.OrderStatus.PENDING) " +
            "ORDER BY o.createdAt DESC")
    List<Order> findUserSuccessOrdersWithDetails(@Param("userId") Integer userId,
                                                 @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    BigDecimal calculateRevenueBetweenDates(@Param("status") OrderStatus status,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal calculateOverallRevenue(@Param("status") OrderStatus status);

    @Query("SELECT COALESCE(COUNT(o), 0) FROM Order o WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    Long countOrdersBetweenDates(@Param("status") OrderStatus status,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(COUNT(o), 0) FROM Order o WHERE o.status = :status")
    Long countOverallOrders(@Param("status") OrderStatus status);

    List<Order> findAllByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<Order> findAllByOrderByCreatedAtDesc();
}
