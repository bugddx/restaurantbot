package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import org.springframework.stereotype.Component;

@Component
public class ReservationHandler implements ConversationHandler {

    @Override
    public ConversationState getState() {
        return ConversationState.RESERVATION;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message
    ) {
        String input = message == null ? "" : message.trim();

        try {
            int guestCount = Integer.parseInt(input);

            if (guestCount <= 0) {
                throw new NumberFormatException();
            }

            conversation.setReservationGuestCount(guestCount);

            return new BotResponse(
                    """
                    📅 Great! Your reservation is for %d people.

                    Please enter the reservation date.

                    Format: YYYY-MM-DD
                    """.formatted(guestCount),
                    ConversationState.RESERVATION_DATE,
                    false
            );

        } catch (NumberFormatException exception) {
            return new BotResponse(
                    """
                    ⚠️ Please enter a valid number of guests.

                    For example:
                    2
                    4
                    6
                    """,
                    ConversationState.RESERVATION,
                    false
            );
        }
    }
}
