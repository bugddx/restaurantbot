package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.entity.OrderStatus;
import com.restaurantbot.order.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmOrderHandler implements ConversationHandler {

    private final OrderService orderService;

    @Override
    public ConversationState getState() {
        return ConversationState.CONFIRM_ORDER;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getCurrentOrder() == null) {

            return new BotResponse(
                    """
                    🛒 Your cart is empty.

                    Please add items before placing an order.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        String input = message == null
                ? ""
                : message.trim();

        if ("1".equals(input)) {

            Long restaurantId =
                    conversation
                            .getRestaurant()
                            .getId();

            Long orderId =
                    conversation
                            .getCurrentOrder()
                            .getId();

            orderService.updateStatus(
                    restaurantId,
                    orderId,
                    new UpdateOrderStatusRequest(
                            OrderStatus.CONFIRMED
                    )
            );

            conversation
                    .getCurrentOrder()
                    .setStatus(OrderStatus.CONFIRMED);

            return new BotResponse(
                    """
                    ✅ Your order has been confirmed!

                    Thank you for your order.
                    We will start preparing it shortly.

                    Order status: CONFIRMED
                    """,
                    ConversationState.PAYMENT,
                    false
            );
        }

        if ("2".equals(input)) {

            return new BotResponse(
                    """
                    👍 Order not placed.

                    Your cart is still available.
                    """,
                    ConversationState.VIEW_CART,
                    false
            );
        }

        return new BotResponse(
                """
                ⚠️ Invalid choice.

                1️⃣ Confirm order
                2️⃣ Go back to cart

                Please reply with 1 or 2.
                """,
                ConversationState.CONFIRM_ORDER,
                false
        );
    }
}
