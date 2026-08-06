package com.restaurantbot.payment.dto;

import com.restaurantbot.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentRequest(

        @NotNull(message = "Payment status is required.")
        PaymentStatus status
) {
}
