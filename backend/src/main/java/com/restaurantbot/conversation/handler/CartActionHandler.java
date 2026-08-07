package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartActionHandler implements ConversationHandler {

    @Override
    public ConversationState getState() {
        return ConversationState.CART_ACTION;
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

        String input = message == null
                ? ""
                : message.trim();

        return switch (input) {

            case "1" -> new BotResponse(
                    """
                    🍽️ Let's add more items.

                    Please choose a category.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );

            case "2" -> new BotResponse(
                    """
                    ✏️ Which item would you like
                    to change the quantity of?

                    Reply with the item number.
                    """,
                    ConversationState.CHANGE_QUANTITY,
                    false
            );

            case "3" -> new BotResponse(
                    """
                    🗑️ Which item would you like
                    to remove?

                    Reply with the item number.
                    """,
                    ConversationState.REMOVE_ITEM,
                    false
            );

            case "4" -> new BotResponse(
                    """
                    ✅ Let's confirm your order.
                    """,
                    ConversationState.CONFIRM_ORDER,
                    false
            );

            case "5" -> {

                conversation.setCurrentOrder(null);
                conversation.setSelectedItem(null);
                conversation.setSelectedOrderItem(null);
                conversation.setSelectedCategory(null);

                yield new BotResponse(
                        """
                        ❌ Your order has been cancelled.

                        You can start a new order anytime.
                        """,
                        ConversationState.MAIN_MENU,
                        false
                );
            }

            default -> new BotResponse(
                    """
                    ⚠️ Invalid option.

                    Please choose:

                    1️⃣ Add more items
                    2️⃣ Change quantity
                    3️⃣ Remove item
                    4️⃣ Place order
                    5️⃣ Cancel order
                    """,
                    ConversationState.CART_ACTION,
                    false
            );
        };
    }
}
