package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.service.OrderService;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.service.OrderItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewCartHandler implements ConversationHandler {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Override
    public ConversationState getState() {
        return ConversationState.VIEW_CART;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getCurrentOrder() == null) {

            return new BotResponse(
                    """
                    🛒 Your cart is empty.

                    Please start by choosing an item.
                    """,
                    ConversationState.MAIN_MENU,
                    false
            );
        }

        Long restaurantId =
                conversation.getRestaurant().getId();

        Long orderId =
                conversation.getCurrentOrder().getId();

        List<OrderItemResponse> items =
                orderItemService.getAll(
                        restaurantId,
                        orderId
                );

        if (items.isEmpty()) {

            return new BotResponse(
                    """
                    🛒 Your cart is empty.

                    Please choose an item from the menu.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        OrderResponse order =
                orderService.getById(
                        restaurantId,
                        orderId
                );

        StringBuilder messageBuilder =
                new StringBuilder();

        messageBuilder
                .append("🛒 *Your Order*")
                .append("\n\n");

        for (int i = 0; i < items.size(); i++) {

            OrderItemResponse item =
                    items.get(i);

            messageBuilder
                    .append(i + 1)
                    .append("️⃣ ")
                    .append(item.menuItemName())
                    .append(" × ")
                    .append(item.quantity())
                    .append(" = ₹")
                    .append(item.subtotal())
                    .append("\n");
        }

        messageBuilder
                .append("\n")
                .append("Subtotal: ₹")
                .append(order.subtotal())
                .append("\n")
                .append("Tax: ₹")
                .append(order.taxAmount())
                .append("\n");

        if (order.discountAmount() != null
                && order.discountAmount().signum() > 0) {

            messageBuilder
                    .append("Discount: -₹")
                    .append(order.discountAmount())
                    .append("\n");
        }

        messageBuilder
                .append("────────────────\n")
                .append("Total: ₹")
                .append(order.totalAmount())
                .append("\n\n")
                .append("What would you like to do next?");

        return new BotResponse(
                messageBuilder.toString(),
                ConversationState.VIEW_CART,
                false
        );
    }
}
