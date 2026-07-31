package com.restaurantbot.restaurant.controller;

import com.restaurantbot.common.response.ApiResponse;
import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.service.RestaurantService;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

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

    @GetMapping("/{id}")
    public ApiResponse<RestaurantResponse> getById(
            @PathVariable Long id
            ) {
        return ApiResponse.<RestaurantResponse>builder()
            .success(true)
            .message("Restaurant found")
            .data(service.findById(id))
            .build();
            }

    @GetMapping
    public ApiResponse<List<RestaurantResponse>> getAll() {
        return ApiResponse.<List<RestaurantResponse>>builder()
            .success(true)
            .message("Restaurants retrieved successfully")
            .data(service.findAll())
            .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RestaurantResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantRequest request
            ) {
        return ApiResponse.<RestaurantResponse>builder()
            .success(true)
            .message("Restaurant updated successfully")
            .data(service.update(id, request))
            .build();
            }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
