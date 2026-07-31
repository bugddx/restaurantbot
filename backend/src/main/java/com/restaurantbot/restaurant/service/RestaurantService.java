package com.restaurantbot.restaurant.service;

import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import java.util.List;

public interface RestaurantService {

    RestaurantResponse create(CreateRestaurantRequest request);

    RestaurantResponse findById(Long id);

    List<RestaurantResponse> findAll();

    RestaurantResponse update(
            Long id,
            UpdateRestaurantRequest request
            );

    void delete(Long id);
}
