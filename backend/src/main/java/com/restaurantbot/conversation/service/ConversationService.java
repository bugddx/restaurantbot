package com.restaurantbot.conversation.service;

import com.restaurantbot.conversation.dto.ConversationResponse;
import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;

public interface ConversationService {

    Conversation createIfAbsent(
            String phoneNumber,
            Long restaurantId
    );

    Conversation findByPhoneNumber(
            String phoneNumber
    );

    ConversationResponse getConversation(
            String phoneNumber
    );

    void reset(
            String phoneNumber
    );

    void updateState(
            String phoneNumber,
            com.restaurantbot.conversation.state.ConversationState state
    );

    BotResponse processMessage(
        String phoneNumber,
        String message,
        Long restaurantId
);
}
