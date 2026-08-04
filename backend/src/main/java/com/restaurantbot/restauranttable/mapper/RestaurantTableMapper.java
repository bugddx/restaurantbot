package com.restaurantbot.restauranttable.mapper;

import com.restaurantbot.restauranttable.dto.CreateRestaurantTableRequest;
import com.restaurantbot.restauranttable.dto.RestaurantTableResponse;
import com.restaurantbot.restauranttable.dto.UpdateRestaurantTableRequest;
import com.restaurantbot.restauranttable.entity.RestaurantTable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RestaurantTableMapper {

    RestaurantTable toEntity(CreateRestaurantTableRequest request);

    RestaurantTableResponse toResponse(RestaurantTable entity);

    void updateEntity(
            UpdateRestaurantTableRequest request,
            @MappingTarget RestaurantTable entity
    );
}
