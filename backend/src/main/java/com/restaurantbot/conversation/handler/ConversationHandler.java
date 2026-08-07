package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;

public interface ConversationHandler {

    ConversationState getState();

    BotResponse handle(
            Conversation conversation,
            String message
    );
}
