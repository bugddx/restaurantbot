package com.restaurantbot.order.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.order.dto.CreateOrderRequest;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.entity.OrderStatus;
import com.restaurantbot.order.mapper.OrderMapper;
import com.restaurantbot.order.repository.OrderRepository;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import com.restaurantbot.restauranttable.entity.RestaurantTable;
import com.restaurantbot.restauranttable.repository.RestaurantTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Restaurant restaurant;
    private RestaurantTable table;
    private Order order;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
            .id(1L)
            .name("Pizza Palace")
            .email("owner@pizza.com")
            .build();

        table = RestaurantTable.builder()
            .id(10L)
            .tableNumber("T1")
            .restaurant(restaurant)
            .build();

        order = Order.builder()
            .id(100L)
            .restaurant(restaurant)
            .table(table)
            .orderNumber("ORD-ABC123")
            .status(OrderStatus.OPEN)
            .subtotal(new BigDecimal("500"))
            .taxAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(new BigDecimal("500"))
            .notes("Extra cheese")
            .build();
    }

    @Test
void create_shouldCreateOrder() {

    CreateOrderRequest request =
            new CreateOrderRequest(
                    table.getId(),
                    "Extra cheese"
            );

    Order mappedOrder = new Order();

    when(restaurantRepository.findById(1L))
            .thenReturn(Optional.of(restaurant));

    when(tableRepository.findByIdAndRestaurantId(
            table.getId(),
            restaurant.getId()))
            .thenReturn(Optional.of(table));

    when(orderMapper.toEntity(request))
            .thenReturn(mappedOrder);

    when(orderRepository.save(mappedOrder))
            .thenReturn(order);

    OrderResponse expected =
            new OrderResponse(
                    100L,
                    "ORD-ABC123",
                    1L,
                    10L,
                    OrderStatus.OPEN,
                    new BigDecimal("500"),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new BigDecimal("500"),
                    "Extra cheese"
            );

    when(orderMapper.toResponse(order))
            .thenReturn(expected);

    OrderResponse response =
            orderService.create(1L, request);

    assertThat(response).isEqualTo(expected);

    verify(orderRepository).save(mappedOrder);
}

@Test
void create_shouldThrow_whenRestaurantNotFound() {

    CreateOrderRequest request =
            new CreateOrderRequest(
                    10L,
                    "Notes"
            );

    when(restaurantRepository.findById(1L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            orderService.create(1L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Restaurant not found");

    verify(orderRepository, never()).save(any());
}

@Test
void create_shouldThrow_whenTableNotFound() {

    CreateOrderRequest request =
            new CreateOrderRequest(
                    10L,
                    "Notes"
            );

    when(restaurantRepository.findById(1L))
            .thenReturn(Optional.of(restaurant));

    when(tableRepository.findByIdAndRestaurantId(
            10L,
            1L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            orderService.create(1L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Restaurant table not found");

    verify(orderRepository, never()).save(any());
}

@Test
void getById_shouldReturnOrder() {

    when(orderRepository.findByIdAndRestaurantId(
            100L,
            1L))
            .thenReturn(Optional.of(order));

    OrderResponse expected =
            new OrderResponse(
                    100L,
                    "ORD-ABC123",
                    1L,
                    10L,
                    OrderStatus.OPEN,
                    new BigDecimal("500"),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new BigDecimal("500"),
                    "Extra cheese"
            );

    when(orderMapper.toResponse(order))
            .thenReturn(expected);

    OrderResponse response =
            orderService.getById(1L, 100L);

    assertThat(response).isEqualTo(expected);
}

@Test
void getById_shouldThrow_whenOrderNotFound() {

    when(orderRepository.findByIdAndRestaurantId(
            100L,
            1L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            orderService.getById(1L, 100L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Order not found");
}

@Test
void getAll_shouldReturnOrders() {

    when(restaurantRepository.existsById(1L))
            .thenReturn(true);

    Page<Order> page =
            new PageImpl<>(List.of(order));

    when(orderRepository.findByRestaurantId(
            eq(1L),
            any(Pageable.class)))
            .thenReturn(page);

    OrderResponse expected =
            new OrderResponse(
                    100L,
                    "ORD-ABC123",
                    1L,
                    10L,
                    OrderStatus.OPEN,
                    new BigDecimal("500"),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new BigDecimal("500"),
                    "Extra cheese"
            );

    when(orderMapper.toResponse(order))
            .thenReturn(expected);

    Page<OrderResponse> response =
            orderService.getAll(
                    1L,
                    PageRequest.of(0, 10));

    assertThat(response.getTotalElements()).isEqualTo(1);
    assertThat(response.getContent().getFirst())
            .isEqualTo(expected);
}

@Test
void getAll_shouldThrow_whenRestaurantNotFound() {

    when(restaurantRepository.existsById(1L))
            .thenReturn(false);

    assertThatThrownBy(() ->
            orderService.getAll(
                    1L,
                    PageRequest.of(0,10)))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Restaurant not found");

    verify(orderRepository, never())
            .findByRestaurantId(anyLong(), any());
}

@Test
void updateStatus_shouldUpdateOrderStatus() {

    UpdateOrderStatusRequest request =
            new UpdateOrderStatusRequest(
                    OrderStatus.PREPARING
            );

    when(orderRepository.findByIdAndRestaurantId(
            100L,
            1L))
            .thenReturn(Optional.of(order));

    doAnswer(invocation -> {
        UpdateOrderStatusRequest req = invocation.getArgument(0);
        Order entity = invocation.getArgument(1);
        entity.setStatus(req.status());
        return null;
    }).when(orderMapper)
            .updateStatus(any(), any());

    when(orderRepository.save(order))
            .thenReturn(order);

    OrderResponse expected =
            new OrderResponse(
                    100L,
                    "ORD-ABC123",
                    1L,
                    10L,
                    OrderStatus.PREPARING,
                    new BigDecimal("500"),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new BigDecimal("500"),
                    "Extra cheese"
            );

    when(orderMapper.toResponse(order))
            .thenReturn(expected);

    OrderResponse response =
            orderService.updateStatus(
                    1L,
                    100L,
                    request);

    assertThat(response.status())
            .isEqualTo(OrderStatus.PREPARING);

    verify(orderRepository).save(order);
}

@Test
void updateStatus_shouldThrow_whenOrderNotFound() {

    UpdateOrderStatusRequest request =
            new UpdateOrderStatusRequest(
                    OrderStatus.PREPARING
            );

    when(orderRepository.findByIdAndRestaurantId(
            100L,
            1L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            orderService.updateStatus(
                    1L,
                    100L,
                    request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Order not found");

    verify(orderRepository, never()).save(any());
}

@Test
void delete_shouldDeleteOrder() {

    when(orderRepository.findByIdAndRestaurantId(
            100L,
            1L))
            .thenReturn(Optional.of(order));

    orderService.delete(1L, 100L);

    verify(orderRepository).delete(order);
}

@Test
void delete_shouldThrow_whenOrderNotFound() {

    when(orderRepository.findByIdAndRestaurantId(
            100L,
            1L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            orderService.delete(
                    1L,
                    100L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Order not found");

    verify(orderRepository, never()).delete(any());
}

}
