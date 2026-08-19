package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.service.OrderItemService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnterNewQuantityHandlerTest {

    @Mock
    private OrderItemService orderItemService;

    private EnterNewQuantityHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        order = Order.builder()
                .id(500L)
                .restaurant(restaurant)
                .build();

        orderItem = OrderItem.builder()
                .id(1000L)
                .order(order)
                .quantity(2)
                .unitPrice(new BigDecimal("299"))
                .subtotal(new BigDecimal("598"))
                .notes(null)
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .selectedOrderItem(orderItem)
                .state(ConversationState.ENTER_NEW_QUANTITY)
                .build();

        handler =
                new EnterNewQuantityHandler(
                        orderItemService
                );
    }

    @Test
    void getState_shouldReturnEnterNewQuantity() {

        assertThat(handler.getState())
                .isEqualTo(
                        ConversationState.ENTER_NEW_QUANTITY
                );
    }

    @Test
    void handle_shouldUpdateQuantity() {

        OrderItemResponse updated =
                new OrderItemResponse(
                        1000L,
                        500L,
                        100L,
                        "Margherita",
                        3,
                        new BigDecimal("299"),
                        new BigDecimal("897"),
                        null
                );

        when(orderItemService.update(
                any(),
                any(),
                any(),
                any(UpdateOrderItemRequest.class)
        )).thenReturn(updated);

        BotResponse response =
                handler.handle(
                        conversation,
                        "3"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_CART
                );

        assertThat(response.message())
                .contains("Quantity updated")
                .contains("Margherita")
                .contains("3")
                .contains("897");

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isNull();

        verify(orderItemService)
                .update(
                        any(),
                        any(),
                        any(),
                        any(UpdateOrderItemRequest.class)
                );
    }

    @Test
    void handle_shouldStayInState_whenQuantityIsZero() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "0"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.ENTER_NEW_QUANTITY
                );

        assertThat(response.message())
                .contains("greater than zero");

        verify(orderItemService, never())
                .update(
                        any(),
                        any(),
                        any(),
                        any(UpdateOrderItemRequest.class)
                );
    }

    @Test
    void handle_shouldStayInState_whenQuantityIsNegative() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "-2"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.ENTER_NEW_QUANTITY
                );

        assertThat(response.message())
                .contains("greater than zero");

        verify(orderItemService, never())
                .update(
                        any(),
                        any(),
                        any(),
                        any(UpdateOrderItemRequest.class)
                );
    }

    @Test
    void handle_shouldStayInState_whenQuantityIsNotNumeric() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "abc"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.ENTER_NEW_QUANTITY
                );

        assertThat(response.message())
                .contains("valid quantity");

        verify(orderItemService, never())
                .update(
                        any(),
                        any(),
                        any(),
                        any(UpdateOrderItemRequest.class)
                );
    }

    @Test
    void handle_shouldGoToChangeQuantity_whenNoSelectedOrderItem() {

        conversation.setSelectedOrderItem(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "3"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CHANGE_QUANTITY
                );

        assertThat(response.message())
                .contains("No order item");

        verify(orderItemService, never())
                .update(
                        any(),
                        any(),
                        any(),
                        any(UpdateOrderItemRequest.class)
                );
    }

    @Test
    void handle_shouldGoToViewMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "3"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_MENU
                );

        assertThat(response.message())
                .contains("cart is empty");

        verify(orderItemService, never())
                .update(
                        any(),
                        any(),
                        any(),
                        any(UpdateOrderItemRequest.class)
                );
    }
}
