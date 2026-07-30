package com.restaurantbot.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(

        @NotBlank(message = "Restaurant name is required")
        @Size(max = 200, message = "Restaurant name must not exceed 200 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Size(max = 30, message = "Phone must not exceed 30 characters")
        String phone,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address
) {
}
