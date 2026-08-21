package com.restaurantbot.menuitem.service;

import com.restaurantbot.menuitem.dto.CreateMenuItemRequest;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.dto.UpdateMenuItemRequest;
import com.restaurantbot.menuitem.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuItemService {

    MenuItemResponse create(
            Long restaurantId,
            CreateMenuItemRequest request
    );

    MenuItemResponse getById(
            Long restaurantId,
            Long itemId
    );

    Page<MenuItemResponse> getAll(
            Long restaurantId,
            Pageable pageable
    );

    Page<MenuItemResponse> getByCategory(
            Long restaurantId,
            Long categoryId,
            Pageable pageable
    );

    MenuItemResponse update(
            Long restaurantId,
            Long itemId,
            UpdateMenuItemRequest request
    );

    void delete(
            Long restaurantId,
            Long itemId
    );

    MenuItem getEntityById(
        Long restaurantId,
        Long itemId
);
}
