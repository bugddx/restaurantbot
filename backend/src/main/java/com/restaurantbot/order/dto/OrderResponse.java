package com.restaurantbot.order.dto;

import com.restaurantbot.order.entity.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(

        Long id,

        String orderNumber,

        Long restaurantId,

        Long tableId,

        OrderStatus status,

        BigDecimal subtotal,

        BigDecimal taxAmount,

        BigDecimal discountAmount,

        BigDecimal totalAmount,

        String notes

) {
}
