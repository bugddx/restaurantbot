package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.restaurant.entity.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationHandlerTest {

    private ReservationHandler handler;
    private Conversation conversation;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        handler = new ReservationHandler();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .state(ConversationState.RESERVATION)
                .build();
    }

    @Test
    void getState_shouldReturnReservation() {
        assertThat(handler.getState())
                .isEqualTo(ConversationState.RESERVATION);
    }

    @Test
    void handle_shouldAcceptValidGuestCount() {
        BotResponse response = handler.handle(conversation, "4");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_DATE);

        assertThat(conversation.getReservationGuestCount())
                .isEqualTo(4);

        assertThat(response.message())
                .contains("4 people")
                .contains("YYYY-MM-DD");
    }

    @Test
    void handle_shouldRejectZeroGuestCount() {
        BotResponse response = handler.handle(conversation, "0");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);

        assertThat(conversation.getReservationGuestCount())
                .isNull();
    }

    @Test
    void handle_shouldRejectNegativeGuestCount() {
        BotResponse response = handler.handle(conversation, "-2");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);
    }

    @Test
    void handle_shouldRejectInvalidGuestCount() {
        BotResponse response = handler.handle(conversation, "abc");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);

        assertThat(response.message())
                .contains("valid number of guests");
    }

    @Test
    void handle_shouldRejectNullInput() {
        BotResponse response = handler.handle(conversation, null);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);
    }
}
