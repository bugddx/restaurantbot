package com.restaurantbot.restauranttable.service;

import com.restaurantbot.restauranttable.dto.CreateRestaurantTableRequest;
import com.restaurantbot.restauranttable.dto.RestaurantTableResponse;
import com.restaurantbot.restauranttable.dto.UpdateRestaurantTableRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantTableService {

    RestaurantTableResponse create(
            Long restaurantId,
            CreateRestaurantTableRequest request
    );

    RestaurantTableResponse getById(
            Long restaurantId,
            Long tableId
    );

    Page<RestaurantTableResponse> getAll(
            Long restaurantId,
            Pageable pageable
    );

    RestaurantTableResponse update(
            Long restaurantId,
            Long tableId,
            UpdateRestaurantTableRequest request
    );

    void delete(
            Long restaurantId,
            Long tableId
    );
}
