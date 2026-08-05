package com.restaurantbot.menucategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateMenuCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order cannot be negative")
        Integer displayOrder,

        @NotNull(message = "Active status is required")
        Boolean active
        ) {
        }
