package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class ReservationTimeHandler implements ConversationHandler {

    private final ReservationService reservationService;

    @Override
    public ConversationState getState() {
        return ConversationState.RESERVATION_TIME;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message
    ) {
        String input = message == null ? "" : message.trim();

        try {
            LocalTime time = LocalTime.parse(input);

            if (conversation.getReservationDate() == null) {
                return new BotResponse(
                        """
                        ⚠️ Reservation date is missing.

                        Let's start the reservation again.
                        """,
                        ConversationState.RESERVATION,
                        false
                );
            }

            if (conversation.getReservationGuestCount() == null) {
                return new BotResponse(
                        """
                        ⚠️ Guest count is missing.

                        Let's start the reservation again.
                        """,
                        ConversationState.RESERVATION,
                        false
                );
            }

            conversation.setReservationTime(time);

            Long restaurantId = conversation.getRestaurant().getId();

            reservationService.create(
                    restaurantId,
                    conversation.getPhoneNumber(),
                    new CreateReservationRequest(
                            conversation.getReservationGuestCount(),
                            conversation.getReservationDate(),
                            time,
                            null
                    )
            );

            return new BotResponse(
                    """
                    ✅ Your table reservation has been requested!

                    👥 Guests: %d
                    📅 Date: %s
                    🕐 Time: %s

                    Your reservation is currently PENDING.

                    Thank you! 🍽️
                    """.formatted(
                            conversation.getReservationGuestCount(),
                            conversation.getReservationDate(),
                            time
                    ),
                    ConversationState.MAIN_MENU,
                    false
            );

        } catch (DateTimeParseException exception) {
            return new BotResponse(
                    """
                    ⚠️ Invalid time.

                    Please enter the time in this format:
                    HH:MM

                    Example: 19:30
                    """,
                    ConversationState.RESERVATION_TIME,
                    false
            );
        }
    }
}
