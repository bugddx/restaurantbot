package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;

import org.springframework.stereotype.Component;

@Component
public class StartHandler implements ConversationHandler {

    @Override
    public ConversationState getState() {
        return ConversationState.START;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        return new BotResponse(
                """
                👋 Welcome to our restaurant!

                I'm your restaurant assistant.

                How can I help you today?

                1️⃣ View Menu
                2️⃣ Order Food
                3️⃣ Reserve a Table
                4️⃣ Track Order
                5️⃣ Help

                Reply with a number to continue.
                """,
                ConversationState.MAIN_MENU,
                false
        );
    }
}
