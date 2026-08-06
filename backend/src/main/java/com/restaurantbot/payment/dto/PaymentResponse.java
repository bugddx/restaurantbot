package com.restaurantbot.payment.dto;

import com.restaurantbot.payment.entity.PaymentMethod;
import com.restaurantbot.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentResponse(

        Long id,

        Long orderId,

        PaymentMethod paymentMethod,

        PaymentStatus status,

        BigDecimal amount,

        String transactionReference,

        OffsetDateTime paidAt
) {
}
