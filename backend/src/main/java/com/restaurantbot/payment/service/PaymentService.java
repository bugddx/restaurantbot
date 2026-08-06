package com.restaurantbot.payment.service;

import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.dto.PaymentResponse;
import com.restaurantbot.payment.dto.UpdatePaymentRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse create(
            Long restaurantId,
            Long orderId,
            CreatePaymentRequest request
    );

    PaymentResponse getById(
            Long restaurantId,
            Long orderId,
            Long paymentId
    );

    List<PaymentResponse> getAll(
            Long restaurantId
    );

    PaymentResponse update(
            Long restaurantId,
            Long orderId,
            Long paymentId,
            UpdatePaymentRequest request
    );

    void delete(
            Long restaurantId,
            Long orderId,
            Long paymentId
    );
}
