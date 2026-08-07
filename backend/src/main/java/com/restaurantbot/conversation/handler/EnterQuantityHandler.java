package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.service.OrderItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterQuantityHandler implements ConversationHandler {

    private final OrderItemService orderItemService;

    @Override
    public ConversationState getState() {
        return ConversationState.ENTER_QUANTITY;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getCurrentOrder() == null) {

            return new BotResponse(
                    """
                    ⚠️ No active order was found.

                    Please start a new order.
                    """,
                    ConversationState.MAIN_MENU,
                    false
            );
        }

        if (conversation.getSelectedItem() == null) {

            return new BotResponse(
                    """
                    ⚠️ No item has been selected.

                    Please choose an item first.
                    """,
                    ConversationState.VIEW_CATEGORY,
                    false
            );
        }

        String input = message == null
                ? ""
                : message.trim();

        int quantity;

        try {
            quantity = Integer.parseInt(input);
        } catch (NumberFormatException e) {

            return new BotResponse(
                    """
                    ⚠️ Please enter a valid quantity.

                    For example: 1, 2, or 3.
                    """,
                    ConversationState.ENTER_QUANTITY,
                    false
            );
        }

        if (quantity <= 0) {

            return new BotResponse(
                    """
                    ⚠️ Quantity must be at least 1.

                    Please enter a valid quantity.
                    """,
                    ConversationState.ENTER_QUANTITY,
                    false
            );
        }

        MenuItem selectedItem =
                conversation.getSelectedItem();

        CreateOrderItemRequest request =
                new CreateOrderItemRequest(
                        selectedItem.getId(),
                        quantity,
                        null
                );

        orderItemService.create(
                conversation.getRestaurant().getId(),
                conversation.getCurrentOrder().getId(),
                request
        );

        return new BotResponse(
                "✅ Added "
                        + quantity
                        + " × "
                        + selectedItem.getName()
                        + " to your order.\n\n"
                        + "What would you like to do next?",
                ConversationState.VIEW_CART,
                false
        );
    }
}
