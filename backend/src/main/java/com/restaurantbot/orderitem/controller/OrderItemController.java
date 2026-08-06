package com.restaurantbot.orderitem.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.orderitem.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemResponse>> create(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody CreateOrderItemRequest request) {

        OrderItemResponse response =
                service.create(
                        restaurantId,
                        orderId,
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        "Order item created successfully.",
                        response
                ));
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {

        OrderItemResponse response =
                service.getById(
                        restaurantId,
                        orderId,
                        orderItemId
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order item retrieved successfully.",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getAll(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        List<OrderItemResponse> response =
                service.getAll(
                        restaurantId,
                        orderId
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order items retrieved successfully.",
                        response
                ));
    }

    @PutMapping("/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> update(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long orderItemId,
            @Valid @RequestBody UpdateOrderItemRequest request) {

        OrderItemResponse response =
                service.update(
                        restaurantId,
                        orderId,
                        orderItemId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order item updated successfully.",
                        response
                ));
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {

        service.delete(
                restaurantId,
                orderId,
                orderItemId
        );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Order item deleted successfully."
                ));
    }
}
