package com.restaurantbot.order.repository;

import com.restaurantbot.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndRestaurantId(
            Long id,
            Long restaurantId
    );

    Page<Order> findByRestaurantId(
            Long restaurantId,
            Pageable pageable
    );

    boolean existsByOrderNumber(
            String orderNumber
    );

}
