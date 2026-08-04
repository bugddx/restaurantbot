package com.restaurantbot.restauranttable.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.restauranttable.dto.CreateRestaurantTableRequest;
import com.restaurantbot.restauranttable.dto.RestaurantTableResponse;
import com.restaurantbot.restauranttable.dto.UpdateRestaurantTableRequest;
import com.restaurantbot.restauranttable.service.RestaurantTableService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/tables")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService service;

    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantTableResponse>> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateRestaurantTableRequest request) {

        RestaurantTableResponse response =
                service.create(restaurantId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        "Restaurant table created successfully.",
                        response));
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<ApiResponse<RestaurantTableResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {

        RestaurantTableResponse response =
                service.getById(restaurantId, tableId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Restaurant table retrieved successfully.",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RestaurantTableResponse>>> getAll(
            @PathVariable Long restaurantId,
            Pageable pageable) {

        Page<RestaurantTableResponse> response =
                service.getAll(restaurantId, pageable);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Restaurant tables retrieved successfully.",
                        response));
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<ApiResponse<RestaurantTableResponse>> update(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @Valid @RequestBody UpdateRestaurantTableRequest request) {

        RestaurantTableResponse response =
                service.update(restaurantId, tableId, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Restaurant table updated successfully.",
                        response));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {

        service.delete(restaurantId, tableId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Restaurant table deleted successfully."));
    }
}
