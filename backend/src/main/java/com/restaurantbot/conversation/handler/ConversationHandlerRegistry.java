package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.state.ConversationState;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ConversationHandlerRegistry {

    private final Map<ConversationState, ConversationHandler> handlers =
            new EnumMap<>(ConversationState.class);

    public ConversationHandlerRegistry(
            List<ConversationHandler> conversationHandlers) {

        conversationHandlers.forEach(handler ->
                handlers.put(
                        handler.getState(),
                        handler
                )
        );
    }

    public ConversationHandler getHandler(
            ConversationState state) {

        ConversationHandler handler =
                handlers.get(state);

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler registered for state: " + state
            );
        }

        return handler;
    }
}
