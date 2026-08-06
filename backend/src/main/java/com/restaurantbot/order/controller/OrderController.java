package com.restaurantbot.order.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.order.dto.CreateOrderRequest;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response =
                orderService.create(restaurantId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        "Order created successfully.",
                        response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        OrderResponse response =
                orderService.getById(restaurantId, orderId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order retrieved successfully.",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAll(
            @PathVariable Long restaurantId,
            Pageable pageable) {

        Page<OrderResponse> response =
                orderService.getAll(restaurantId, pageable);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Orders retrieved successfully.",
                        response));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response =
                orderService.updateStatus(
                        restaurantId,
                        orderId,
                        request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order status updated successfully.",
                        response));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        orderService.delete(restaurantId, orderId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order deleted successfully."));
    }
}
