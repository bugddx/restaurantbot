package com.restaurantbot.menuitem.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(

        @NotNull(message = "Category id is required")
        Long categoryId,

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", inclusive = false,
                message = "Price must be greater than zero")
        BigDecimal price,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        @Positive(message = "Preparation time must be positive")
        Integer preparationTime,

        @NotNull(message = "Vegetarian flag is required")
        Boolean vegetarian,

        @NotNull(message = "Vegan flag is required")
        Boolean vegan,

        @NotNull(message = "Availability is required")
        Boolean available,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}
