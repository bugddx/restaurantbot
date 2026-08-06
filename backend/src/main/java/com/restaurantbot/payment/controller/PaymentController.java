package com.restaurantbot.payment.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.dto.PaymentResponse;
import com.restaurantbot.payment.dto.UpdatePaymentRequest;
import com.restaurantbot.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/orders/{orderId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response =
                service.create(
                        restaurantId,
                        orderId,
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        "Payment created successfully.",
                        response
                ));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long paymentId) {

        PaymentResponse response =
                service.getById(
                        restaurantId,
                        orderId,
                        paymentId
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Payment retrieved successfully.",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAll(
            @PathVariable Long restaurantId) {

        List<PaymentResponse> response =
                service.getAll(
                        restaurantId
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Payments retrieved successfully.",
                        response
                ));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> update(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long paymentId,
            @Valid @RequestBody UpdatePaymentRequest request) {

        PaymentResponse response =
                service.update(
                        restaurantId,
                        orderId,
                        paymentId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Payment updated successfully.",
                        response
                ));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long paymentId) {

        service.delete(
                restaurantId,
                orderId,
                paymentId
        );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Payment deleted successfully."
                ));
    }
}
