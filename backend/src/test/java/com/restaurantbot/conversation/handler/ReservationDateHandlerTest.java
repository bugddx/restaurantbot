package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.restaurant.entity.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDateHandlerTest {

    private ReservationDateHandler handler;
    private Conversation conversation;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        handler = new ReservationDateHandler();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .state(ConversationState.RESERVATION_DATE)
                .reservationGuestCount(4)
                .build();
    }

    @Test
    void getState_shouldReturnReservationDate() {
        assertThat(handler.getState())
                .isEqualTo(ConversationState.RESERVATION_DATE);
    }

    @Test
    void handle_shouldAcceptFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(5);

        BotResponse response =
                handler.handle(conversation, futureDate.toString());

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_TIME);

        assertThat(conversation.getReservationDate())
                .isEqualTo(futureDate);

        assertThat(response.message())
                .contains("reservation time")
                .contains("HH:MM");
    }

    @Test
    void handle_shouldRejectToday() {
        BotResponse response =
                handler.handle(conversation, LocalDate.now().toString());

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_DATE);

        assertThat(conversation.getReservationDate())
                .isNull();
    }

    @Test
    void handle_shouldRejectPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        BotResponse response =
                handler.handle(conversation, pastDate.toString());

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_DATE);
    }

    @Test
    void handle_shouldRejectInvalidDate() {
        BotResponse response =
                handler.handle(conversation, "15-09-2026");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_DATE);

        assertThat(response.message())
                .contains("Invalid date");
    }

    @Test
    void handle_shouldRejectNullInput() {
        BotResponse response =
                handler.handle(conversation, null);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_DATE);
    }
}
