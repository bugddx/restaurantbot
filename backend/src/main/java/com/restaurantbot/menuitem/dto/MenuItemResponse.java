package com.restaurantbot.menuitem.dto;

import java.math.BigDecimal;

public record MenuItemResponse(

        Long id,

        Long restaurantId,

        Long categoryId,

        String categoryName,

        String name,

        String description,

        BigDecimal price,

        String imageUrl,

        Integer preparationTime,

        Boolean vegetarian,

        Boolean vegan,

        Boolean available,

        Integer displayOrder
) {
}
