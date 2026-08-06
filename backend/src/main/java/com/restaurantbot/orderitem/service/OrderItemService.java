package com.restaurantbot.orderitem.service;

import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;

import java.util.List;

public interface OrderItemService {

    OrderItemResponse create(
            Long restaurantId,
            Long orderId,
            CreateOrderItemRequest request
    );

    OrderItemResponse getById(
            Long restaurantId,
            Long orderId,
            Long orderItemId
    );

    List<OrderItemResponse> getAll(
            Long restaurantId,
            Long orderId
    );

    OrderItemResponse update(
            Long restaurantId,
            Long orderId,
            Long orderItemId,
            UpdateOrderItemRequest request
    );

    void delete(
            Long restaurantId,
            Long orderId,
            Long orderItemId
    );
}
