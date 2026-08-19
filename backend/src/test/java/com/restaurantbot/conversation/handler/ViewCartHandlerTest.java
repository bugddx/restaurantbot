package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.entity.OrderStatus;
import com.restaurantbot.order.service.OrderService;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.service.OrderItemService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViewCartHandlerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    private ViewCartHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private MenuCategory category;

    private MenuItem item;

    private Order order;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        category = MenuCategory.builder()
                .id(10L)
                .restaurant(restaurant)
                .name("Pizza")
                .build();

        item = MenuItem.builder()
                .id(100L)
                .restaurant(restaurant)
                .category(category)
                .name("Margherita")
                .price(new BigDecimal("299"))
                .available(true)
                .build();

        order = Order.builder()
                .id(500L)
                .restaurant(restaurant)
                .status(OrderStatus.OPEN)
                .subtotal(new BigDecimal("598"))
                .taxAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("598"))
                .build();

        conversation = Conversation.builder()
                .id(1000L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .selectedCategory(category)
                .selectedItem(item)
                .state(ConversationState.VIEW_CART)
                .build();

        handler = new ViewCartHandler(
                orderService,
                orderItemService
        );
    }

    @Test
    void getState_shouldReturnViewCart() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.VIEW_CART);
    }

    @Test
    void handle_shouldDisplayCart() {

        OrderItemResponse orderItem =
                new OrderItemResponse(
                        1000L,
                        500L,
                        100L,
                        "Margherita",
                        2,
                        new BigDecimal("299"),
                        new BigDecimal("598"),
                        null
                );

        OrderResponse orderResponse =
                new OrderResponse(
                        500L,
                        "ORD-ABC123",
                        1L,
                        null,
                        OrderStatus.OPEN,
                        new BigDecimal("598"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("598"),
                        null
                );

        when(orderItemService.getAll(
                1L,
                500L
        )).thenReturn(
                List.of(orderItem)
        );

        when(orderService.getById(
                1L,
                500L
        )).thenReturn(orderResponse);

        BotResponse response =
                handler.handle(
                        conversation,
                        ""
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_CART);

        assertThat(response.message())
                .contains("Your Order")
                .contains("Margherita")
                .contains("2")
                .contains("₹598")
                .contains("Subtotal")
                .contains("Total");

        verify(orderItemService)
                .getAll(1L, 500L);

        verify(orderService)
                .getById(1L, 500L);
    }

    @Test
    void handle_shouldDisplayMultipleItems() {

        OrderItemResponse pizza =
                new OrderItemResponse(
                        1000L,
                        500L,
                        100L,
                        "Margherita",
                        2,
                        new BigDecimal("299"),
                        new BigDecimal("598"),
                        null
                );

        OrderItemResponse coke =
                new OrderItemResponse(
                        1001L,
                        500L,
                        200L,
                        "Coke",
                        1,
                        new BigDecimal("80"),
                        new BigDecimal("80"),
                        null
                );

        OrderResponse orderResponse =
                new OrderResponse(
                        500L,
                        "ORD-ABC123",
                        1L,
                        null,
                        OrderStatus.OPEN,
                        new BigDecimal("678"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("678"),
                        null
                );

        when(orderItemService.getAll(
                1L,
                500L
        )).thenReturn(
                List.of(
                        pizza,
                        coke
                )
        );

        when(orderService.getById(
                1L,
                500L
        )).thenReturn(orderResponse);

        BotResponse response =
                handler.handle(
                        conversation,
                        ""
                );

        assertThat(response.message())
                .contains("Margherita")
                .contains("Coke")
                .contains("598")
                .contains("80")
                .contains("678");
    }

    @Test
    void handle_shouldDisplayDiscount_whenDiscountExists() {

        OrderItemResponse itemResponse =
                new OrderItemResponse(
                        1000L,
                        500L,
                        100L,
                        "Margherita",
                        2,
                        new BigDecimal("299"),
                        new BigDecimal("598"),
                        null
                );

        OrderResponse orderResponse =
                new OrderResponse(
                        500L,
                        "ORD-ABC123",
                        1L,
                        null,
                        OrderStatus.OPEN,
                        new BigDecimal("598"),
                        BigDecimal.ZERO,
                        new BigDecimal("50"),
                        new BigDecimal("548"),
                        null
                );

        when(orderItemService.getAll(
                1L,
                500L
        )).thenReturn(
                List.of(itemResponse)
        );

        when(orderService.getById(
                1L,
                500L
        )).thenReturn(orderResponse);

        BotResponse response =
                handler.handle(
                        conversation,
                        ""
                );

        assertThat(response.message())
                .contains("Discount")
                .contains("-₹50")
                .contains("₹548");
    }

    @Test
    void handle_shouldReturnToMainMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        ""
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.MAIN_MENU);

        assertThat(response.message())
                .contains("cart is empty");

        verify(orderItemService, never())
                .getAll(
                        eq(1L),
                        eq(500L)
                );

        verify(orderService, never())
                .getById(
                        eq(1L),
                        eq(500L)
                );
    }

    @Test
    void handle_shouldReturnToMenu_whenOrderHasNoItems() {

        when(orderItemService.getAll(
                1L,
                500L
        )).thenReturn(
                List.of()
        );

        BotResponse response =
                handler.handle(
                        conversation,
                        ""
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("cart is empty");

        verify(orderItemService)
                .getAll(1L, 500L);

        verify(orderService, never())
                .getById(
                        eq(1L),
                        eq(500L)
                );
    }
}
