package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.service.MenuCategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewMenuHandler implements ConversationHandler {

    private final MenuCategoryService menuCategoryService;

    @Override
    public ConversationState getState() {
        return ConversationState.VIEW_MENU;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        Long restaurantId =
                conversation.getRestaurant().getId();

        List<MenuCategoryResponse> categories =
                menuCategoryService
                        .getAll(
                                restaurantId,
                                PageRequest.of(0, 50)
                        )
                        .getContent();

        List<MenuCategoryResponse> activeCategories =
                categories.stream()
                        .filter(category ->
                                Boolean.TRUE.equals(
                                        category.active()
                                ))
                        .toList();

        if (activeCategories.isEmpty()) {

            return new BotResponse(
                    """
                    😔 Sorry, our menu is currently unavailable.

                    Please try again later.
                    """,
                    ConversationState.MAIN_MENU,
                    false
            );
        }

        StringBuilder messageBuilder =
                new StringBuilder();

        messageBuilder.append(
                "🍽️ Please choose a category:\n\n"
        );

        for (int i = 0; i < activeCategories.size(); i++) {

            MenuCategoryResponse category =
                    activeCategories.get(i);

            messageBuilder
                    .append(i + 1)
                    .append("️⃣ ")
                    .append(category.name())
                    .append("\n");
        }

        messageBuilder.append(
                "\nReply with a number."
        );

        return new BotResponse(
                messageBuilder.toString(),
                ConversationState.SELECT_CATEGORY,
                false
        );
    }
}
