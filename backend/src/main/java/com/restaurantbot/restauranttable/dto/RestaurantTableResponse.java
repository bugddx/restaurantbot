package com.restaurantbot.restauranttable.dto;

import com.restaurantbot.restauranttable.entity.TableStatus;

import java.util.UUID;

public record RestaurantTableResponse(
        Long id,
        Long restaurantId,
        String tableNumber,
        UUID qrToken,
        TableStatus status,
        String whatsappLink
) {
}
