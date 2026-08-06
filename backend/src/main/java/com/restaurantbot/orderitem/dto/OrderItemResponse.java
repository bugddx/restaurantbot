package com.restaurantbot.orderitem.dto;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long id,

        Long orderId,

        Long menuItemId,

        String menuItemName,

        Integer quantity,

        BigDecimal unitPrice,

        BigDecimal subtotal,

        String notes
) {
}
