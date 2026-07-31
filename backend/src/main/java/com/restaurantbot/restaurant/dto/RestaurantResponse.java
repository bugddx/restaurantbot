package com.restaurantbot.restaurant.dto;

import java.util.UUID;

public record RestaurantResponse(

        Long id,

        UUID public_id,

        String name,

        String email,

        String phone,

        String address

) {
}
