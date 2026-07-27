package com.restaurantbot.restaurant.dto;

import java.util.UUID;

public record RestaurantResponse(

        UUID id,

        String name,

        String email,

        String phone,

        String address

) {
}
