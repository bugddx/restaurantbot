package com.restaurantbot.restauranttable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRestaurantTableRequest(

        @NotBlank(message = "Table number is required")
        @Size(max = 30, message = "Table number must not exceed 30 characters")
        String tableNumber
) {
}
