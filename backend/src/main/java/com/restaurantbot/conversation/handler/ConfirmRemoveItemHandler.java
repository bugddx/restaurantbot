package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.orderitem.service.OrderItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmRemoveItemHandler
    implements ConversationHandler {

    private final OrderItemService orderItemService;

    @Override
    public ConversationState getState() {
        return ConversationState.CONFIRM_REMOVE_ITEM;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getCurrentOrder() == null) {

            return new BotResponse(
                    """
                    🛒 Your cart is empty.

                    Please choose an item from the menu.
                    """,
                    ConversationState.VIEW_MENU,
                    false
                    );
        }

        if (conversation.getSelectedOrderItem() == null) {

            return new BotResponse(
                    """
                    ⚠️ No item has been selected.

                    Please choose an item from your cart.
                    """,
                    ConversationState.REMOVE_ITEM,
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

            Long orderItemId =
                conversation
                .getSelectedOrderItem()
                .getId();

            String itemName =
                conversation
                .getSelectedOrderItem()
                .getMenuItem()
                .getName();

            orderItemService.delete(
                    restaurantId,
                    orderId,
                    orderItemId
                    );

            conversation.setSelectedOrderItem(null);

            return new BotResponse(
                    """
                    🗑️ %s has been removed from your cart.

                    Your cart has been updated.
                    """.formatted(itemName),
                    ConversationState.VIEW_CART,
                    false
                    );

        }

        if ("2".equals(input)) {

            conversation.setSelectedOrderItem(null);

            return new BotResponse(
                    """
                    👍 Item was not removed.

                    Your cart remains unchanged.
                    """,
                    ConversationState.VIEW_CART,
                    false
                    );
        }

        return new BotResponse(
                """
                ⚠️ Invalid choice.

                1️⃣ Yes, remove it
                2️⃣ No, keep it

                Please reply with 1 or 2.
                """,
                ConversationState.CONFIRM_REMOVE_ITEM,
                false
                );
            }
}
