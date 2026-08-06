package com.restaurantbot.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(

        @NotNull(message = "Table is required")
        Long tableId,

        @Size(max = 500,
                message = "Notes must not exceed 500 characters")
        String notes

) {
}
