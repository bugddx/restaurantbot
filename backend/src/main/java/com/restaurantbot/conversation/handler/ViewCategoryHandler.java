package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.service.MenuItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewCategoryHandler implements ConversationHandler {

    private final MenuItemService menuItemService;

    @Override
    public ConversationState getState() {
        return ConversationState.VIEW_CATEGORY;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getSelectedCategory() == null) {
            return new BotResponse(
                    """
                    ⚠️ No category has been selected.

                    Please choose a category from the menu.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        Long restaurantId =
                conversation.getRestaurant().getId();

        Long categoryId =
                conversation.getSelectedCategory().getId();

        List<MenuItemResponse> items =
                menuItemService
                        .getByCategory(
                                restaurantId,
                                categoryId,
                                PageRequest.of(0, 50)
                        )
                        .getContent();

        List<MenuItemResponse> availableItems =
                items.stream()
                        .filter(item ->
                                Boolean.TRUE.equals(
                                        item.available()
                                ))
                        .toList();

        if (availableItems.isEmpty()) {

            return new BotResponse(
                    """
                    😔 Sorry, there are no available items
                    in this category right now.

                    Please choose another category.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        StringBuilder messageBuilder =
                new StringBuilder();

        messageBuilder
                .append("🍽️ ")
                .append(
                        conversation
                                .getSelectedCategory()
                                .getName()
                )
                .append("\n\n");

        for (int i = 0; i < availableItems.size(); i++) {

            MenuItemResponse item =
                    availableItems.get(i);

            messageBuilder
                    .append(i + 1)
                    .append("️⃣ ")
                    .append(item.name())
                    .append(" - ₹")
                    .append(item.price())
                    .append("\n");

            if (item.description() != null
                    && !item.description().isBlank()) {

                messageBuilder
                        .append("   ")
                        .append(item.description())
                        .append("\n");
            }
        }

        messageBuilder.append(
                "\nReply with a number to select an item."
        );

        return new BotResponse(
                messageBuilder.toString(),
                ConversationState.SELECT_ITEM,
                false
        );
    }
}
