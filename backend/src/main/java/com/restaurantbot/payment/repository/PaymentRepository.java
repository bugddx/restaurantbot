package com.restaurantbot.payment.repository;

import com.restaurantbot.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByIdAndOrderId(
            Long id,
            Long orderId
    );

    boolean existsByOrderId(Long orderId);
}
