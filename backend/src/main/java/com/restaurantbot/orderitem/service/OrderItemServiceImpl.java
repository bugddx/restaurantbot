package com.restaurantbot.orderitem.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.mapper.OrderItemMapper;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menuitem.repository.MenuItemRepository;
import com.restaurantbot.order.repository.OrderRepository;
import com.restaurantbot.orderitem.repository.OrderItemRepository;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import com.restaurantbot.restauranttable.entity.RestaurantTable;
import com.restaurantbot.restauranttable.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemMapper mapper;

    private void recalculateOrderTotals(Order order) {

        BigDecimal subtotal =
            orderItemRepository.calculateSubtotal(order.getId());

        order.setSubtotal(subtotal);

        BigDecimal tax =
            subtotal.multiply(BigDecimal.ZERO);

        order.setTaxAmount(tax);

        order.setTotalAmount(
                subtotal
                .add(tax)
                .subtract(order.getDiscountAmount())
                );

        orderRepository.save(order);
    }

    @Override
    public OrderItemResponse create(
            Long restaurantId,
            Long orderId,
            CreateOrderItemRequest request) {

        Order order =
            orderRepository.findByIdAndRestaurantId(
                    orderId,
                    restaurantId
                    ).orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        MenuItem menuItem =
            menuItemRepository.findByIdAndRestaurantId(
                    request.menuItemId(),
                    restaurantId
                    ).orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));

        OrderItem orderItem =
            mapper.toEntity(request);

        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);

        orderItem.setUnitPrice(menuItem.getPrice());

        BigDecimal subtotal =
            menuItem.getPrice()
            .multiply(
                    BigDecimal.valueOf(
                        request.quantity()
                        )
                    );

        orderItem.setSubtotal(subtotal);

        OrderItem saved =
            orderItemRepository.save(orderItem);

        recalculateOrderTotals(order);

        return mapper.toResponse(saved);
            }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponse getById(
            Long restaurantId,
            Long orderId,
            Long orderItemId) {

        OrderItem orderItem =
            orderItemRepository.findByIdAndOrderId(
                    orderItemId,
                    orderId
                    ).orElseThrow(() ->
                        new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getOrder()
                .getRestaurant()
                .getId()
                .equals(restaurantId)) {

            throw new ResourceNotFoundException("Order item not found");
                }

        return mapper.toResponse(orderItem);
            }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getAll(
            Long restaurantId,
            Long orderId) {

        Order order =
            orderRepository.findByIdAndRestaurantId(
                    orderId,
                    restaurantId
                    ).orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return orderItemRepository.findByOrderId(order.getId())
            .stream()
            .map(mapper::toResponse)
            .toList();
            }

    @Override
    public OrderItemResponse update(
            Long restaurantId,
            Long orderId,
            Long orderItemId,
            UpdateOrderItemRequest request) {

        OrderItem orderItem =
            orderItemRepository.findByIdAndOrderId(
                    orderItemId,
                    orderId
                    ).orElseThrow(() ->
                        new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getOrder()
                .getRestaurant()
                .getId()
                .equals(restaurantId)) {

            throw new ResourceNotFoundException("Order item not found");
                }

        mapper.update(request, orderItem);

        orderItem.setSubtotal(
                orderItem.getUnitPrice()
                .multiply(
                    BigDecimal.valueOf(
                        orderItem.getQuantity()
                        )
                    )
                );

        OrderItem saved =
            orderItemRepository.save(orderItem);

        recalculateOrderTotals(orderItem.getOrder());

        return mapper.toResponse(saved);
            }

    @Override
    public void delete(
            Long restaurantId,
            Long orderId,
            Long orderItemId) {

        OrderItem orderItem =
            orderItemRepository.findByIdAndOrderId(
                    orderItemId,
                    orderId
                    ).orElseThrow(() ->
                        new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getOrder()
                .getRestaurant()
                .getId()
                .equals(restaurantId)) {

            throw new ResourceNotFoundException("Order item not found");
                }

        Order order = orderItem.getOrder();

        orderItemRepository.delete(orderItem);

        recalculateOrderTotals(order);
            }

}
