package com.restaurantbot.restaurant.service;

import com.restaurantbot.common.exception.RestaurantAlreadyExistsException;
import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;

    @Override
    public RestaurantResponse create(CreateRestaurantRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new RestaurantAlreadyExistsException(request.email());
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();

        restaurant = repository.save(restaurant);

        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getEmail(),
                restaurant.getPhone(),
                restaurant.getAddress()
        );
    }
}
