package com.restaurantbot.menucategory.dto;

public record MenuCategoryResponse(

        Long id,
        Long restaurantId,
        String name,
        Integer displayOrder,
        Boolean active
) {
}
