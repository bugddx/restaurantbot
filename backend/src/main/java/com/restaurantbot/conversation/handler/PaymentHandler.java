package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.entity.PaymentMethod;
import com.restaurantbot.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHandler implements ConversationHandler {

    private final PaymentService paymentService;

    @Override
    public ConversationState getState() {
        return ConversationState.PAYMENT;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getCurrentOrder() == null) {

            return new BotResponse(
                    """
                    🛒 No active order found.

                    Please start a new order.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        String input = message == null
                ? ""
                : message.trim();

        PaymentMethod paymentMethod;

        switch (input) {

            case "1" -> paymentMethod = PaymentMethod.CASH;

            case "2" -> paymentMethod = PaymentMethod.UPI;

            case "3" -> paymentMethod = PaymentMethod.CARD;

            default -> {
                return new BotResponse(
                        """
                        💳 Please choose a payment method:

                        1️⃣ Cash
                        2️⃣ UPI
                        3️⃣ Card

                        Reply with 1, 2, or 3.
                        """,
                        ConversationState.PAYMENT,
                        false
                );
            }
        }

        Long restaurantId =
                conversation
                        .getRestaurant()
                        .getId();

        Long orderId =
                conversation
                        .getCurrentOrder()
                        .getId();

        paymentService.create(
                restaurantId,
                orderId,
                new CreatePaymentRequest(
                        paymentMethod,
                        null
                )
        );

        return new BotResponse(
                """
                ✅ Payment recorded successfully!

                Payment method: %s

                💰 Amount: ₹%s

                Your order has been placed successfully.
                Thank you for ordering with us! 🍽️
                """.formatted(
                        paymentMethod,
                        conversation
                                .getCurrentOrder()
                                .getTotalAmount()
                ),
                ConversationState.COMPLETE,
                false
        );
    }
}
