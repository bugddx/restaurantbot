package com.restaurantbot.payment.dto;

import com.restaurantbot.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePaymentRequest(

        @NotNull(message = "Payment method is required.")
        PaymentMethod paymentMethod,

        @Size(max = 100, message = "Transaction reference cannot exceed 100 characters.")
        String transactionReference
) {
}
