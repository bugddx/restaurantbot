package com.restaurantbot.orderitem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderItemRequest(

        @NotNull(message = "Menu item id is required.")
        Long menuItemId,

        @NotNull(message = "Quantity is required.")
        @Min(value = 1, message = "Quantity must be at least 1.")
        Integer quantity,

        @Size(max = 255, message = "Notes cannot exceed 255 characters.")
        String notes
) {
}
