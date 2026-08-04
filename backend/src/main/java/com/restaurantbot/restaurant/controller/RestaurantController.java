package com.restaurantbot.restaurant.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.service.RestaurantService;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;

    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantResponse>> create(
            @Valid @RequestBody CreateRestaurantRequest request) {

            RestaurantResponse response = service.create(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                            "Restaurant created successfully.",
                            response));
            }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getById(
            @PathVariable Long id
            ) {

        RestaurantResponse response = service.findById(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                    "Restaurant retrieved successfully.",
                    response
                    ));
            }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAll() {
        List<RestaurantResponse> restaurants = service.findAll();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                    "Restaurants retrieved successfully.",
                    restaurants
                    )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantRequest request
            ) {

        RestaurantResponse response =   service.update(id, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                    "Restaurant updated successfully.",
                    response
                    )
                );
            }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id){
            service.delete(id);

            return ResponseEntity.ok(
                    ApiResponseUtil.success(
                        "Restaurant deleted successfully."
                        )
                    );
            }
}
