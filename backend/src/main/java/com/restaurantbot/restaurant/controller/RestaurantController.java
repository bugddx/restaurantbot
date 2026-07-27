package com.restaurantbot.restaurant.controller;

import com.restaurantbot.common.response.ApiResponse;
import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RestaurantResponse> create(
            @Valid @RequestBody CreateRestaurantRequest request
    ) {

        return ApiResponse.<RestaurantResponse>builder()
                .success(true)
                .message("Restaurant created successfully")
                .data(service.create(request))
                .build();
    }
}
