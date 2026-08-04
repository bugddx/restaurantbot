package com.restaurantbot.restauranttable.dto;

import com.restaurantbot.restauranttable.entity.TableStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantTableRequest(

        @NotBlank(message = "Table number is required")
        @Size(max = 30, message = "Table number must not exceed 30 characters")
        String tableNumber,

        @NotNull(message = "Table status is required")
        TableStatus status

) {
}
