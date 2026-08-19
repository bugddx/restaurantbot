package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.entity.PaymentMethod;
import com.restaurantbot.payment.service.PaymentService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PaymentHandlerTest {

    @Mock
    private PaymentService paymentService;

    private PaymentHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .build();

        order = Order.builder()
                .id(100L)
                .restaurant(restaurant)
                .totalAmount(new java.math.BigDecimal("450.00"))
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .state(ConversationState.PAYMENT)
                .build();

        handler = new PaymentHandler(paymentService);
    }

    @Test
    void getState_shouldReturnPayment() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.PAYMENT);
    }

    @Test
    void handle_shouldCreateCashPayment() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(paymentService).create(
                eq(1L),
                eq(100L),
                eq(new CreatePaymentRequest(
                        PaymentMethod.CASH,
                        null
                ))
        );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.COMPLETE);

        assertThat(response.message())
                .contains("Payment recorded successfully")
                .contains("CASH")
                .contains("450.00");
    }

    @Test
    void handle_shouldCreateUpiPayment() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        verify(paymentService).create(
                eq(1L),
                eq(100L),
                eq(new CreatePaymentRequest(
                        PaymentMethod.UPI,
                        null
                ))
        );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.COMPLETE);

        assertThat(response.message())
                .contains("UPI");
    }

    @Test
    void handle_shouldCreateCardPayment() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "3"
                );

        verify(paymentService).create(
                eq(1L),
                eq(100L),
                eq(new CreatePaymentRequest(
                        PaymentMethod.CARD,
                        null
                ))
        );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.COMPLETE);

        assertThat(response.message())
                .contains("CARD");
    }

    @Test
    void handle_shouldStayInPaymentForInvalidInput() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "9"
                );

        verify(paymentService, never())
                .create(
                        any(),
                        any(),
                        any()
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.PAYMENT);

        assertThat(response.message())
                .contains("Please choose a payment method");
    }

    @Test
    void handle_shouldStayInPaymentForNullInput() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        verify(paymentService, never())
                .create(
                        any(),
                        any(),
                        any()
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.PAYMENT);
    }

    @Test
    void handle_shouldGoToViewMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(paymentService, never())
                .create(
                        any(),
                        any(),
                        any()
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("No active order");
    }
}
