package com.restaurantbot.conversation.dto;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.state.ConversationState;

public record BotResponse(

        String message,

        ConversationState nextState,

        boolean endConversation
) {
}
