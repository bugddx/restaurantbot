package com.restaurantbot.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(

        @NotBlank
        String name,

        @Email
        String email,

        String phone,

        String address

) {
}
