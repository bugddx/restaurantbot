package com.restaurantbot.orderitem.repository;

import com.restaurantbot.orderitem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByIdAndOrderId(Long id, Long orderId);

    boolean existsByIdAndOrderId(Long id, Long orderId);

    long countByOrderId(Long orderId);

    @Query("""
        SELECT COALESCE(SUM(oi.subtotal), 0)
        FROM OrderItem oi
        WHERE oi.order.id = :orderId
        """)
    BigDecimal calculateSubtotal(@Param("orderId") Long orderId);
}
