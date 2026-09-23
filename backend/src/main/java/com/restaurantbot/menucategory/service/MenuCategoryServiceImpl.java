package com.restaurantbot.menucategory.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menucategory.dto.CreateMenuCategoryRequest;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.dto.UpdateMenuCategoryRequest;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.menucategory.mapper.MenuCategoryMapper;
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
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryMapper menuCategoryMapper;

    @Override
    public MenuCategoryResponse create(
            Long restaurantId,
            CreateMenuCategoryRequest request) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        if (menuCategoryRepository.existsByRestaurantIdAndName(
                restaurantId,
                request.name())) {

            throw new ResourceAlreadyExistsException(
                    "Category already exists for this restaurant");
        }

        MenuCategory category = menuCategoryMapper.toEntity(request);
        category.setRestaurant(restaurant);

        MenuCategory saved = menuCategoryRepository.save(category);

        return menuCategoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuCategoryResponse getById(
            Long restaurantId,
            Long categoryId) {

        MenuCategory category = menuCategoryRepository
                .findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu category not found"));

        return menuCategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuCategoryResponse> getAll(
            Long restaurantId,
            Pageable pageable) {

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found");
        }

        return menuCategoryRepository
        .findByRestaurantIdOrderByDisplayOrderAsc(
                restaurantId,
                pageable
        )
        .map(menuCategoryMapper::toResponse);
    }

    @Override
    public MenuCategoryResponse update(
            Long restaurantId,
            Long categoryId,
            UpdateMenuCategoryRequest request) {

        MenuCategory category = menuCategoryRepository
                .findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu category not found"));

        if (menuCategoryRepository
                .existsByRestaurantIdAndNameAndIdNot(
                        restaurantId,
                        request.name(),
                        categoryId)) {

            throw new ResourceAlreadyExistsException(
                    "Category already exists for this restaurant");
        }

        menuCategoryMapper.updateEntity(request, category);

        MenuCategory updated = menuCategoryRepository.save(category);

        return menuCategoryMapper.toResponse(updated);
    }

    @Override
    public void delete(
            Long restaurantId,
            Long categoryId) {

        MenuCategory category = menuCategoryRepository
                .findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu category not found"));

        menuCategoryRepository.delete(category);
    }

    @Override
@Transactional(readOnly = true)
public MenuCategory getEntityById(
        Long restaurantId,
        Long categoryId) {

    return menuCategoryRepository
            .findByIdAndRestaurantId(
                    categoryId,
                    restaurantId
            )
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Menu category not found"
                    ));
}
}
