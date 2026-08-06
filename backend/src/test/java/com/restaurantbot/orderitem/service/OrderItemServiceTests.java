package com.restaurantbot.orderitem.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menuitem.repository.MenuItemRepository;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.repository.OrderRepository;
import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.mapper.OrderItemMapper;
import com.restaurantbot.orderitem.repository.OrderItemRepository;
import com.restaurantbot.orderitem.service.OrderItemServiceImpl;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private OrderItemMapper mapper;

    @InjectMocks
    private OrderItemServiceImpl service;

    private Restaurant restaurant;
    private Order order;
    private MenuItem menuItem;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
            .id(1L)
            .name("Pizza Palace")
            .build();

        order = Order.builder()
            .id(10L)
            .restaurant(restaurant)
            .subtotal(BigDecimal.ZERO)
            .taxAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(BigDecimal.ZERO)
            .build();

        menuItem = MenuItem.builder()
            .id(100L)
            .name("Margherita Pizza")
            .price(new BigDecimal("299"))
            .restaurant(restaurant)
            .build();

        orderItem = OrderItem.builder()
            .id(1L)
            .order(order)
            .menuItem(menuItem)
            .quantity(2)
            .unitPrice(new BigDecimal("299"))
            .subtotal(new BigDecimal("598"))
            .notes("Extra cheese")
            .build();
    }


@Test
void create_shouldCreateOrderItem() {

    CreateOrderItemRequest request =
        new CreateOrderItemRequest(
                100L,
                2,
                "Extra cheese"
                );

    OrderItem mapped = new OrderItem();

    OrderItemResponse response =
        new OrderItemResponse(
                1L,
                10L,
                100L,
                "Margherita Pizza",
                2,
                new BigDecimal("299"),
                new BigDecimal("598"),
                "Extra cheese"
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(menuItemRepository.findByIdAndRestaurantId(
                100L,
                1L))
        .thenReturn(Optional.of(menuItem));

    when(mapper.toEntity(request))
        .thenReturn(mapped);

    when(orderItemRepository.save(mapped))
        .thenReturn(orderItem);

    when(orderItemRepository.calculateSubtotal(10L))
        .thenReturn(new BigDecimal("598"));

    when(mapper.toResponse(orderItem))
        .thenReturn(response);

    OrderItemResponse result =
        service.create(
                1L,
                10L,
                request
                );

    assertThat(result).isEqualTo(response);

    verify(orderItemRepository).save(mapped);
    verify(orderRepository).save(order);
}

@Test
void create_shouldThrow_whenOrderNotFound() {

    CreateOrderItemRequest request =
        new CreateOrderItemRequest(
                100L,
                2,
                "Extra cheese"
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.create(
                1L,
                10L,
                request
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Order not found");

    verify(orderItemRepository, never())
        .save(any());
}

@Test
void create_shouldThrow_whenMenuItemNotFound() {

    CreateOrderItemRequest request =
        new CreateOrderItemRequest(
                100L,
                2,
                "Extra cheese"
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(menuItemRepository.findByIdAndRestaurantId(
                100L,
                1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.create(
                1L,
                10L,
                request
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Menu item not found");

    verify(orderItemRepository, never())
        .save(any());
}

@Test
void getById_shouldReturnOrderItem() {

    OrderItemResponse response =
        new OrderItemResponse(
                1L,
                10L,
                100L,
                "Margherita Pizza",
                2,
                new BigDecimal("299"),
                new BigDecimal("598"),
                "Extra cheese"
                );

    when(orderItemRepository.findByIdAndOrderId(
                1L,
                10L))
        .thenReturn(Optional.of(orderItem));

    when(mapper.toResponse(orderItem))
        .thenReturn(response);

    OrderItemResponse result =
        service.getById(
                1L,
                10L,
                1L
                );

    assertThat(result).isEqualTo(response);
}

@Test
void getById_shouldThrow_whenOrderItemNotFound() {

    when(orderItemRepository.findByIdAndOrderId(
                1L,
                10L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getById(
                1L,
                10L,
                1L
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Order item not found");
}

@Test
void getAll_shouldReturnOrderItems() {

    OrderItemResponse response =
        new OrderItemResponse(
                1L,
                10L,
                100L,
                "Margherita Pizza",
                2,
                new BigDecimal("299"),
                new BigDecimal("598"),
                "Extra cheese"
                );

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.of(order));

    when(orderItemRepository.findByOrderId(10L))
        .thenReturn(List.of(orderItem));

    when(mapper.toResponse(orderItem))
        .thenReturn(response);

    List<OrderItemResponse> result =
        service.getAll(
                1L,
                10L
                );

    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).isEqualTo(response);
}

@Test
void getAll_shouldThrow_whenOrderNotFound() {

    when(orderRepository.findByIdAndRestaurantId(
                10L,
                1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getAll(
                1L,
                10L
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Order not found");
}

@Test
void update_shouldUpdateOrderItem() {

    UpdateOrderItemRequest request =
        new UpdateOrderItemRequest(
                3,
                "No onions"
                );

    OrderItemResponse response =
        new OrderItemResponse(
                1L,
                10L,
                100L,
                "Margherita Pizza",
                3,
                new BigDecimal("299"),
                new BigDecimal("897"),
                "No onions"
                );

    when(orderItemRepository.findByIdAndOrderId(
                1L,
                10L))
        .thenReturn(Optional.of(orderItem));

    doAnswer(invocation -> {
        OrderItem entity = invocation.getArgument(1);
        entity.setQuantity(3);
        entity.setNotes("No onions");
        return null;
    }).when(mapper).update(any(), any());

    when(orderItemRepository.save(orderItem))
        .thenReturn(orderItem);

    when(orderItemRepository.calculateSubtotal(10L))
        .thenReturn(new BigDecimal("897"));

    when(mapper.toResponse(orderItem))
        .thenReturn(response);

    OrderItemResponse result =
        service.update(
                1L,
                10L,
                1L,
                request
                );

    assertThat(result).isEqualTo(response);

    verify(orderRepository).save(order);
}

@Test
void update_shouldThrow_whenOrderItemNotFound() {

    UpdateOrderItemRequest request =
        new UpdateOrderItemRequest(
                3,
                "No onions"
                );

    when(orderItemRepository.findByIdAndOrderId(
                1L,
                10L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.update(
                1L,
                10L,
                1L,
                request
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Order item not found");
}

@Test
void delete_shouldDeleteOrderItem() {

    when(orderItemRepository.findByIdAndOrderId(
                1L,
                10L))
        .thenReturn(Optional.of(orderItem));

    when(orderItemRepository.calculateSubtotal(10L))
        .thenReturn(BigDecimal.ZERO);

    service.delete(
            1L,
            10L,
            1L
            );

    verify(orderItemRepository).delete(orderItem);
    verify(orderRepository).save(order);
}

@Test
void delete_shouldThrow_whenOrderItemNotFound() {

    when(orderItemRepository.findByIdAndOrderId(
                1L,
                10L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.delete(
                1L,
                10L,
                1L
                ))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Order item not found");
}

}
