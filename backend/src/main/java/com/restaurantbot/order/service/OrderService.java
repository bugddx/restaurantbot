package com.restaurantbot.order.service;

import com.restaurantbot.order.dto.CreateOrderRequest;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(
            Long restaurantId,
            CreateOrderRequest request
    );

    OrderResponse getById(
            Long restaurantId,
            Long orderId
    );

    Page<OrderResponse> getAll(
            Long restaurantId,
            Pageable pageable
    );

    OrderResponse updateStatus(
            Long restaurantId,
            Long orderId,
            UpdateOrderStatusRequest request
    );

    void delete(
            Long restaurantId,
            Long orderId
    );
}
