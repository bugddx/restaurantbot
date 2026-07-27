package com.restaurantbot.common.exception;

public class RestaurantAlreadyExistsException extends RuntimeException {

    public RestaurantAlreadyExistsException(String email) {
        super("Restaurant already exists with email: " + email);
    }
}
