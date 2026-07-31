package com.restaurantbot.restaurant.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(Long id) {
        super("Restaurant not found with id: " + id);
    }
}
