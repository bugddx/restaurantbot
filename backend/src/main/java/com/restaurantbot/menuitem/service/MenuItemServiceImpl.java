package com.restaurantbot.menuitem.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menuitem.dto.CreateMenuItemRequest;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.dto.UpdateMenuItemRequest;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menuitem.mapper.MenuItemMapper;
import com.restaurantbot.menuitem.repository.MenuItemRepository;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.menucategory.repository.MenuCategoryRepository;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public MenuItemResponse create(
            Long restaurantId,
            CreateMenuItemRequest request) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Restaurant not found"));

        MenuCategory category = menuCategoryRepository
            .findById(request.categoryId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Menu category not found"));

        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException(
                    "Menu category does not belong to the restaurant");
        }

        if (menuItemRepository.existsByCategoryIdAndName(
                    category.getId(),
                    request.name())) {

            throw new ResourceAlreadyExistsException(
                    "Menu item already exists in this category");
                    }

        MenuItem item = menuItemMapper.toEntity(request);

        item.setRestaurant(restaurant);
        item.setCategory(category);

        MenuItem saved = menuItemRepository.save(item);

        return menuItemMapper.toResponse(saved);
            }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getById(
            Long restaurantId,
            Long itemId) {

        MenuItem item = menuItemRepository
            .findByIdAndRestaurantId(itemId, restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Menu item not found"));

        return menuItemMapper.toResponse(item);
            }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getAll(
            Long restaurantId,
            Pageable pageable) {

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found");
        }

        return menuItemRepository
            .findByRestaurantId(restaurantId, pageable)
            .map(menuItemMapper::toResponse);
            }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getByCategory(
            Long restaurantId,
            Long categoryId,
            Pageable pageable) {

        MenuCategory category = menuCategoryRepository
            .findById(categoryId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Menu category not found"));

        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException(
                    "Menu category does not belong to the restaurant");
        }

        return menuItemRepository
            .findByRestaurantIdAndCategoryId(
                    restaurantId,
                    categoryId,
                    pageable)
            .map(menuItemMapper::toResponse);
            }

    @Override
    public MenuItemResponse update(
            Long restaurantId,
            Long itemId,
            UpdateMenuItemRequest request) {

        MenuItem item = menuItemRepository
            .findByIdAndRestaurantId(itemId, restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Menu item not found"));

        MenuCategory category = menuCategoryRepository
            .findById(request.categoryId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Menu category not found"));

        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException(
                    "Menu category does not belong to the restaurant");
        }

        if (menuItemRepository.existsByCategoryIdAndNameAndIdNot(
                    category.getId(),
                    request.name(),
                    itemId)) {

            throw new ResourceAlreadyExistsException(
                    "Menu item already exists in this category");
                    }

        menuItemMapper.updateEntity(request, item);

        item.setCategory(category);

        MenuItem updated = menuItemRepository.save(item);

        return menuItemMapper.toResponse(updated);
            }

    @Override
    public void delete(
            Long restaurantId,
            Long itemId) {

        MenuItem item = menuItemRepository
            .findByIdAndRestaurantId(itemId, restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Menu item not found"));

        menuItemRepository.delete(item);
            }
}
