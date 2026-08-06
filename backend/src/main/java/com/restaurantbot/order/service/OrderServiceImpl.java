package com.restaurantbot.order.service;

import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.order.dto.CreateOrderRequest;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.mapper.OrderMapper;
import com.restaurantbot.order.repository.OrderRepository;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import com.restaurantbot.restauranttable.entity.RestaurantTable;
import com.restaurantbot.restauranttable.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse create(
            Long restaurantId,
            CreateOrderRequest request) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        RestaurantTable table = tableRepository
                .findByIdAndRestaurantId(
                        request.tableId(),
                        restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant table not found"));

        Order order = orderMapper.toEntity(request);

        order.setRestaurant(restaurant);
        order.setTable(table);

        Order saved = orderRepository.save(order);

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(
            Long restaurantId,
            Long orderId) {

        Order order = orderRepository
                .findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAll(
            Long restaurantId,
            Pageable pageable) {

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found");
        }

        return orderRepository
                .findByRestaurantId(restaurantId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse updateStatus(
            Long restaurantId,
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order = orderRepository
                .findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        orderMapper.updateStatus(request, order);

        Order updated = orderRepository.save(order);

        return orderMapper.toResponse(updated);
    }

    @Override
    public void delete(
            Long restaurantId,
            Long orderId) {

        Order order = orderRepository
                .findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        orderRepository.delete(order);
    }
}
