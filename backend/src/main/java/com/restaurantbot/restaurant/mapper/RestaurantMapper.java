package com.restaurantbot.restaurant.mapper;

import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import org.mapstruct.Mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    Restaurant toEntity(CreateRestaurantRequest request);

    RestaurantResponse toResponse(Restaurant restaurant);

    void updateEntity(
            UpdateRestaurantRequest request,
            @MappingTarget Restaurant restaurant
            );
}
