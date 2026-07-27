package com.restaurantbot.restaurant.service;

import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;

public interface RestaurantService {

    RestaurantResponse create(CreateRestaurantRequest request);

}
