package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.order.entity.OrderStatus;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.service.OrderService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyLong;

class ConfirmOrderHandlerTest {

    @Mock
    private OrderService orderService;

    private ConfirmOrderHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;

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
                .status(OrderStatus.OPEN)
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .state(ConversationState.CONFIRM_ORDER)
                .build();

        handler =
                new ConfirmOrderHandler(
                        orderService
                );
    }

    @Test
    void getState_shouldReturnConfirmOrder() {

        assertThat(handler.getState())
                .isEqualTo(
                        ConversationState.CONFIRM_ORDER
                );
    }

    @Test
    void handle_shouldConfirmOrder_whenUserConfirms() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(orderService)
                .updateStatus(
                        1L,
                        500L,
                        new UpdateOrderStatusRequest(
                                OrderStatus.CONFIRMED
                        )
                );

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.CONFIRMED);

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.PAYMENT
                );

        assertThat(response.message())
                .contains("confirmed")
                .contains("CONFIRMED");
    }

    @Test
    void handle_shouldReturnToCart_whenUserCancelsConfirmation() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        verify(orderService, never())
                .updateStatus(
                        anyLong(),
                        anyLong(),
                        org.mockito.ArgumentMatchers.any()
                );

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.OPEN);

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_CART
                );

        assertThat(response.message())
                .contains("not placed");
    }

    @Test
    void handle_shouldStayInConfirmOrder_whenInvalidChoice() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "5"
                );

        verify(orderService, never())
                .updateStatus(
                        anyLong(),
                        anyLong(),
                        org.mockito.ArgumentMatchers.any()
                );

        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.OPEN);

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CONFIRM_ORDER
                );

        assertThat(response.message())
                .contains("Invalid choice");
    }

    @Test
    void handle_shouldGoToViewMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(orderService, never())
                .updateStatus(
                        anyLong(),
                        anyLong(),
                        org.mockito.ArgumentMatchers.any()
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_MENU
                );

        assertThat(response.message())
                .contains("cart is empty");
    }

    @Test
    void handle_shouldTreatNullMessageAsInvalid() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        verify(orderService, never())
                .updateStatus(
                        anyLong(),
                        anyLong(),
                        org.mockito.ArgumentMatchers.any()
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CONFIRM_ORDER
                );

        assertThat(response.message())
                .contains("Invalid choice");
    }
}
