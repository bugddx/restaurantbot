package com.restaurantbot.restaurant.service;

import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.common.exception.RestaurantAlreadyExistsException;
import com.restaurantbot.restaurant.exception.RestaurantNotFoundException;
import com.restaurantbot.restaurant.mapper.RestaurantMapper;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;
    private final RestaurantMapper mapper;

    @Override
    public RestaurantResponse create(CreateRestaurantRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new RestaurantAlreadyExistsException(request.email());
        }

        Restaurant restaurant = mapper.toEntity(request);

        Restaurant savedRestaurant = repository.save(restaurant);

        return mapper.toResponse(savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse findById(UUID id) {

        Restaurant restaurant = repository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        return mapper.toResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

     @Override
    public RestaurantResponse update(
            UUID id,
            UpdateRestaurantRequest request
    ) {
        Restaurant restaurant = findRestaurant(id);

        boolean emailUsedByAnotherRestaurant =
                repository.existsByEmailAndIdNot(request.email(), id);

        if (emailUsedByAnotherRestaurant) {
            throw new RestaurantAlreadyExistsException(request.email());
        }

        mapper.updateEntity(request, restaurant);

        Restaurant updatedRestaurant = repository.save(restaurant);

        return mapper.toResponse(updatedRestaurant);
    }

    @Override
    public void delete(UUID id) {
        Restaurant restaurant = findRestaurant(id);
        repository.delete(restaurant);
    }

    private Restaurant findRestaurant(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }
}
