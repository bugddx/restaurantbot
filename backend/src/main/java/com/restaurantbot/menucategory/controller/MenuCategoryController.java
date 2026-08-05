package com.restaurantbot.menucategory.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.menucategory.dto.CreateMenuCategoryRequest;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.dto.UpdateMenuCategoryRequest;
import com.restaurantbot.menucategory.service.MenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-categories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService service;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateMenuCategoryRequest request) {

        MenuCategoryResponse response =
                service.create(restaurantId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        "Menu category created successfully.",
                        response));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId) {

        MenuCategoryResponse response =
                service.getById(restaurantId, categoryId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu category retrieved successfully.",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MenuCategoryResponse>>> getAll(
            @PathVariable Long restaurantId,
            Pageable pageable) {

        Page<MenuCategoryResponse> response =
                service.getAll(restaurantId, pageable);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu categories retrieved successfully.",
                        response));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> update(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateMenuCategoryRequest request) {

        MenuCategoryResponse response =
                service.update(
                        restaurantId,
                        categoryId,
                        request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu category updated successfully.",
                        response));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId) {

        service.delete(restaurantId, categoryId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu category deleted successfully."));
    }
}
