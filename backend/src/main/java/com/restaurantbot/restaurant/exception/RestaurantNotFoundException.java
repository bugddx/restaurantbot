package com.restaurantbot.restaurant.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(UUID id) {
        super("Restaurant not found with id: " + id);
    }
}
