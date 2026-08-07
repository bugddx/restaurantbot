package com.restaurantbot.conversation.dto;

import com.restaurantbot.conversation.state.ConversationState;

import java.time.OffsetDateTime;

public record ConversationResponse(

        Long id,

        String phoneNumber,

        ConversationState state,

        Long currentOrderId,

        Long selectedCategoryId,

        Long selectedItemId,

        OffsetDateTime lastInteractionAt
) {
}
