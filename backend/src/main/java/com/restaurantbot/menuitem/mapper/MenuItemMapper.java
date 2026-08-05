package com.restaurantbot.menuitem.mapper;

import com.restaurantbot.menuitem.dto.CreateMenuItemRequest;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.dto.UpdateMenuItemRequest;
import com.restaurantbot.menuitem.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "category", ignore = true)
    MenuItem toEntity(CreateMenuItemRequest request);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    MenuItemResponse toResponse(MenuItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntity(
            UpdateMenuItemRequest request,
            @MappingTarget MenuItem entity
    );
}
