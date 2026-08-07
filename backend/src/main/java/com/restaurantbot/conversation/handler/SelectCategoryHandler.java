package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.menucategory.service.MenuCategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SelectCategoryHandler implements ConversationHandler {

    private final MenuCategoryService menuCategoryService;

    @Override
    public ConversationState getState() {
        return ConversationState.SELECT_CATEGORY;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        String input = message == null
                ? ""
                : message.trim();

        int selectedNumber;

        try {
            selectedNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {

            return new BotResponse(
                    """
                    ⚠️ Please enter a valid category number.

                    Reply with the number shown in the menu.
                    """,
                    ConversationState.SELECT_CATEGORY,
                    false
            );
        }

        Long restaurantId =
                conversation.getRestaurant().getId();

        List<MenuCategoryResponse> categories =
                menuCategoryService
                        .getAll(
                                restaurantId,
                                PageRequest.of(0, 50)
                        )
                        .getContent()
                        .stream()
                        .filter(category ->
                                Boolean.TRUE.equals(
                                        category.active()
                                ))
                        .toList();

        if (selectedNumber < 1
                || selectedNumber > categories.size()) {

            return new BotResponse(
                    """
                    ⚠️ Invalid category selection.

                    Please choose one of the numbers
                    shown in the menu.
                    """,
                    ConversationState.SELECT_CATEGORY,
                    false
            );
        }

        MenuCategoryResponse selectedCategory =
                categories.get(selectedNumber - 1);

        MenuCategory category =
                menuCategoryService.getEntityById(
                        restaurantId,
                        selectedCategory.id()
                );

        conversation.setSelectedCategory(category);

        return new BotResponse(
                "🍽️ You selected "
                        + category.getName()
                        + ".",
                ConversationState.VIEW_CATEGORY,
                false
        );
    }
}
