package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class ReservationDateHandler implements ConversationHandler {

    @Override
    public ConversationState getState() {
        return ConversationState.RESERVATION_DATE;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message
    ) {
        String input = message == null ? "" : message.trim();

        try {
            LocalDate date = LocalDate.parse(input);

            if (!date.isAfter(LocalDate.now())) {
                return new BotResponse(
                        """
                        ⚠️ Please choose a future date.

                        Format: YYYY-MM-DD
                        """,
                        ConversationState.RESERVATION_DATE,
                        false
                );
            }

            conversation.setReservationDate(date);

            return new BotResponse(
                    """
                    📅 Reservation date: %s

                    Now please enter the reservation time.

                    Format: HH:MM
                    Example: 19:30
                    """.formatted(date),
                    ConversationState.RESERVATION_TIME,
                    false
            );

        } catch (DateTimeParseException exception) {
            return new BotResponse(
                    """
                    ⚠️ Invalid date.

                    Please enter the date in this format:
                    YYYY-MM-DD

                    Example: 2026-09-15
                    """,
                    ConversationState.RESERVATION_DATE,
                    false
            );
        }
    }
}
