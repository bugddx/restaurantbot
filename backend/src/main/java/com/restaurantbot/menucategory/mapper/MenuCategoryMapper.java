package com.restaurantbot.menucategory.mapper;

import com.restaurantbot.menucategory.dto.CreateMenuCategoryRequest;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.dto.UpdateMenuCategoryRequest;
import com.restaurantbot.menucategory.entity.MenuCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuCategoryMapper {

    MenuCategory toEntity(CreateMenuCategoryRequest request);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    MenuCategoryResponse toResponse(MenuCategory entity);

    void updateEntity(
            UpdateMenuCategoryRequest request,
            @MappingTarget MenuCategory entity
    );
}
