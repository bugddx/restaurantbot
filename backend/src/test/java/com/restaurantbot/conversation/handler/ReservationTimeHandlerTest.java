package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.service.ReservationService;
import com.restaurantbot.restaurant.entity.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservationTimeHandlerTest {

    @Mock
    private ReservationService reservationService;

    private ReservationTimeHandler handler;
    private Conversation conversation;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        handler = new ReservationTimeHandler(reservationService);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .state(ConversationState.RESERVATION_TIME)
                .reservationGuestCount(4)
                .reservationDate(LocalDate.now().plusDays(5))
                .build();

        when(reservationService.create(
                eq(1L),
                eq("919876543210"),
                any()
        )).thenReturn(
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        conversation.getReservationDate(),
                        LocalTime.of(19, 30),
                        com.restaurantbot.reservation.entity.ReservationStatus.PENDING,
                        null
                )
        );
    }

    @Test
    void getState_shouldReturnReservationTime() {
        assertThat(handler.getState())
                .isEqualTo(ConversationState.RESERVATION_TIME);
    }

    @Test
    void handle_shouldCreateReservationForValidTime() {
        BotResponse response =
                handler.handle(conversation, "19:30");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.MAIN_MENU);

        assertThat(conversation.getReservationTime())
                .isEqualTo(LocalTime.of(19, 30));

        assertThat(response.message())
                .contains("table reservation")
                .contains("PENDING");

        verify(reservationService).create(
                eq(1L),
                eq("919876543210"),
                any()
        );
    }

    @Test
    void handle_shouldRejectInvalidTime() {
        BotResponse response =
                handler.handle(conversation, "invalid");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_TIME);

        assertThat(conversation.getReservationTime())
                .isNull();
    }

    @Test
    void handle_shouldRejectNullInput() {
        BotResponse response =
                handler.handle(conversation, null);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION_TIME);
    }

    @Test
    void handle_shouldRestartWhenDateIsMissing() {
        conversation.setReservationDate(null);

        BotResponse response =
                handler.handle(conversation, "19:30");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);

        verify(reservationService, org.mockito.Mockito.never())
                .create(any(), any(), any());
    }

    @Test
    void handle_shouldRestartWhenGuestCountIsMissing() {
        conversation.setReservationGuestCount(null);

        BotResponse response =
                handler.handle(conversation, "19:30");

        assertThat(response.nextState())
                .isEqualTo(ConversationState.RESERVATION);

        verify(reservationService, org.mockito.Mockito.never())
                .create(any(), any(), any());
    }
}
