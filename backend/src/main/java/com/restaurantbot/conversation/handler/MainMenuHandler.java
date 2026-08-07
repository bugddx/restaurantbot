package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;

import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler implements ConversationHandler {

    @Override
    public ConversationState getState() {
        return ConversationState.MAIN_MENU;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        String input = message == null
                ? ""
                : message.trim();

        return switch (input) {

            case "1" ->
                    new BotResponse(
                            """
                            🍽️ Our Menu

                            Choose a category:

                            1️⃣ Pizza
                            2️⃣ Burgers
                            3️⃣ Drinks
                            4️⃣ Desserts

                            Reply with a number.
                            """,
                            ConversationState.VIEW_MENU,
                            false
                    );

            case "2" ->
                    new BotResponse(
                            """
                            🛒 Let's place your order!

                            Choose a category:

                            1️⃣ Pizza
                            2️⃣ Burgers
                            3️⃣ Drinks
                            4️⃣ Desserts

                            Reply with a number.
                            """,
                            ConversationState.VIEW_MENU,
                            false
                    );

            case "3" ->
                    new BotResponse(
                            """
                            🪑 Let's reserve a table.

                            Please tell me how many people
                            the reservation is for.
                            """,
                            ConversationState.RESERVATION,
                            false
                    );

            case "4" ->
                    new BotResponse(
                            """
                            📦 To track your order, please
                            provide your order number.
                            """,
                            ConversationState.COMPLETE,
                            false
                    );

            case "5" ->
                    new BotResponse(
                            """
                            ℹ️ I can help you with:

                            1️⃣ Ordering food
                            2️⃣ Reserving a table
                            3️⃣ Tracking an order

                            Reply with a number to continue.
                            """,
                            ConversationState.MAIN_MENU,
                            false
                    );

            default ->
                    new BotResponse(
                            """
                            I didn't understand that.

                            Please choose one of these options:

                            1️⃣ View Menu
                            2️⃣ Order Food
                            3️⃣ Reserve Table
                            4️⃣ Track Order
                            5️⃣ Help
                            """,
                            ConversationState.MAIN_MENU,
                            false
                    );
        };
    }
}
