package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CompleteHandlerTest {

    private CompleteHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;


    @BeforeEach
    void setUp() {

        handler = new CompleteHandler();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .build();

        order = Order.builder()
                .id(100L)
                .restaurant(restaurant)
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .state(ConversationState.COMPLETE)
                .build();
    }


    @Test
    void getState_shouldReturnComplete() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.COMPLETE);
    }


    @Test
    void handle_shouldReturnCompleteMessageForInvalidInput() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "9"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.COMPLETE);

        assertThat(response.message())
                .contains("Your order has been completed successfully")
                .contains("Start a new order")
                .contains("No, thank you");
    }


    @Test
    void handle_shouldReturnCompleteMessageForNullInput() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.COMPLETE);

        assertThat(response.message())
                .contains("Your order has been completed successfully");
    }


    @Test
    void handle_shouldStartNewOrder_whenInputIsOne() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("Starting a new order");

        assertThat(conversation.getCurrentOrder())
                .isNull();

        assertThat(conversation.getSelectedCategory())
                .isNull();

        assertThat(conversation.getSelectedItem())
                .isNull();

        assertThat(conversation.getSelectedOrderItem())
                .isNull();
    }


    @Test
    void handle_shouldStayComplete_whenInputIsTwo() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.COMPLETE);

        assertThat(response.message())
                .contains("Thank you for ordering with us");

        assertThat(conversation.getCurrentOrder())
                .isNotNull();

        assertThat(conversation.getCurrentOrder().getId())
                .isEqualTo(100L);
    }


    @Test
    void handle_shouldTrimInput() {

        BotResponse response =
                handler.handle(
                        conversation,
                        " 1 "
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(conversation.getCurrentOrder())
                .isNull();
    }
}
