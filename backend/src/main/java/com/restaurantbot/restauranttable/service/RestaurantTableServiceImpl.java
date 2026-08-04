package com.restaurantbot.restauranttable.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import com.restaurantbot.restauranttable.dto.CreateRestaurantTableRequest;
import com.restaurantbot.restauranttable.dto.RestaurantTableResponse;
import com.restaurantbot.restauranttable.dto.UpdateRestaurantTableRequest;
import com.restaurantbot.restauranttable.entity.RestaurantTable;
import com.restaurantbot.restauranttable.mapper.RestaurantTableMapper;
import com.restaurantbot.restauranttable.repository.RestaurantTableRepository;
import com.restaurantbot.restauranttable.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableMapper restaurantTableMapper;

    @Override
    public RestaurantTableResponse create(
            Long restaurantId,
            CreateRestaurantTableRequest request) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Restaurant not found with id: "+restaurantId));

        if (restaurantTableRepository.existsByRestaurantIdAndTableNumber(
                    restaurantId,
                    request.tableNumber())) {

            throw new ResourceAlreadyExistsException(
                    "Table number already exists for this restaurant");
                    }

        RestaurantTable table = restaurantTableMapper.toEntity(request);
        table.setRestaurant(restaurant);

        RestaurantTable saved = restaurantTableRepository.save(table);

        return restaurantTableMapper.toResponse(saved);
            }

    @Override
    @Transactional(readOnly = true)
    public RestaurantTableResponse getById(
            Long restaurantId,
            Long tableId) {

        RestaurantTable table = restaurantTableRepository
            .findByIdAndRestaurantId(tableId, restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Restaurant table not found"));

        return restaurantTableMapper.toResponse(table);
            }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantTableResponse> getAll(
            Long restaurantId,
            Pageable pageable) {

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: "+restaurantId);
        }

        return restaurantTableRepository
            .findByRestaurantId(restaurantId, pageable)
            .map(restaurantTableMapper::toResponse);
            }

    @Override
    public RestaurantTableResponse update(
            Long restaurantId,
            Long tableId,
            UpdateRestaurantTableRequest request) {

        RestaurantTable table = restaurantTableRepository
            .findByIdAndRestaurantId(tableId, restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Restaurant table not found"));

        if (restaurantTableRepository
                .existsByRestaurantIdAndTableNumberAndIdNot(
                    restaurantId,
                    request.tableNumber(),
                    tableId)) {

            throw new ResourceAlreadyExistsException(
                    "Table number already exists for this restaurant");
                    }

        restaurantTableMapper.updateEntity(request, table);

        RestaurantTable updated = restaurantTableRepository.save(table);

        return restaurantTableMapper.toResponse(updated);
            }

    @Override
    public void delete(
            Long restaurantId,
            Long tableId) {

        RestaurantTable table = restaurantTableRepository
            .findByIdAndRestaurantId(tableId, restaurantId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Restaurant table not found"));

        restaurantTableRepository.delete(table);
            }
}
