package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import org.springframework.stereotype.Component;

@Component
public class CompleteHandler implements ConversationHandler {

    @Override
    public ConversationState getState() {
        return ConversationState.COMPLETE;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        String input = message == null
                ? ""
                : message.trim();

        if ("1".equals(input)) {

            conversation.setCurrentOrder(null);
            conversation.setSelectedCategory(null);
            conversation.setSelectedItem(null);
            conversation.setSelectedOrderItem(null);

            return new BotResponse(
                    """
                    🍽️ Starting a new order.

                    Please choose a category.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        if ("2".equals(input)) {

            return new BotResponse(
                    """
                    👋 Thank you for ordering with us!

                    Have a great day!
                    """,
                    ConversationState.COMPLETE,
                    false
            );
        }

        return new BotResponse(
                """
                🎉 Your order has been completed successfully!

                Would you like to order something else?

                1️⃣ Start a new order
                2️⃣ No, thank you
                """,
                ConversationState.COMPLETE,
                false
        );
    }
}
