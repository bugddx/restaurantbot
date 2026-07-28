package com.restaurantbot.restaurant.service;

import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import java.util.UUID;
import java.util.List;

public interface RestaurantService {

    RestaurantResponse create(CreateRestaurantRequest request);

    RestaurantResponse findById(UUID id);

    List<RestaurantResponse> findAll();
}
