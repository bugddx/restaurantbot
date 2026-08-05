package com.restaurantbot.menucategory.service;

import com.restaurantbot.menucategory.dto.CreateMenuCategoryRequest;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.dto.UpdateMenuCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuCategoryService {

    MenuCategoryResponse create(
            Long restaurantId,
            CreateMenuCategoryRequest request
    );

    MenuCategoryResponse getById(
            Long restaurantId,
            Long categoryId
    );

    Page<MenuCategoryResponse> getAll(
            Long restaurantId,
            Pageable pageable
    );

    MenuCategoryResponse update(
            Long restaurantId,
            Long categoryId,
            UpdateMenuCategoryRequest request
    );

    void delete(
            Long restaurantId,
            Long categoryId
    );
}
