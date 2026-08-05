package com.restaurantbot.menuitem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.menuitem.dto.CreateMenuItemRequest;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.dto.UpdateMenuItemRequest;
import com.restaurantbot.menuitem.service.MenuItemService;



@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService service;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuItemResponse>> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateMenuItemRequest request) {

        MenuItemResponse response = service.create(restaurantId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        "Menu item created successfully.",
                        response));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {

        MenuItemResponse response =
                service.getById(restaurantId, itemId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu item retrieved successfully.",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> getAll(
            @PathVariable Long restaurantId,
            Pageable pageable) {

        Page<MenuItemResponse> response =
                service.getAll(restaurantId, pageable);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu items retrieved successfully.",
                        response));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> getByCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId,
            Pageable pageable) {

        Page<MenuItemResponse> response =
                service.getByCategory(
                        restaurantId,
                        categoryId,
                        pageable);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu items retrieved successfully.",
                        response));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> update(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateMenuItemRequest request) {

        MenuItemResponse response =
                service.update(
                        restaurantId,
                        itemId,
                        request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu item updated successfully.",
                        response));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {

        service.delete(restaurantId, itemId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Menu item deleted successfully."));
    }
}
