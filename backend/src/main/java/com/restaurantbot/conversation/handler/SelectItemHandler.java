package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menuitem.service.MenuItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SelectItemHandler implements ConversationHandler {

    private final MenuItemService menuItemService;

    @Override
    public ConversationState getState() {
        return ConversationState.SELECT_ITEM;
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
                    ⚠️ Please enter a valid item number.

                    Reply with the number shown in the menu.
                    """,
                    ConversationState.SELECT_ITEM,
                    false
            );
        }

        if (conversation.getSelectedCategory() == null) {

            return new BotResponse(
                    """
                    ⚠️ No category has been selected.

                    Please choose a category first.
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
                        .getContent()
                        .stream()
                        .filter(item ->
                                Boolean.TRUE.equals(
                                        item.available()
                                ))
                        .toList();

        if (selectedNumber < 1
                || selectedNumber > items.size()) {

            return new BotResponse(
                    """
                    ⚠️ Invalid item selection.

                    Please choose one of the numbers
                    shown in the menu.
                    """,
                    ConversationState.SELECT_ITEM,
                    false
            );
        }

        MenuItemResponse selectedItem =
                items.get(selectedNumber - 1);

        MenuItem item =
                menuItemService.getEntityById(
                        restaurantId,
                        selectedItem.id()
                );

        conversation.setSelectedItem(item);

        return new BotResponse(
                "🍽️ You selected "
                        + item.getName()
                        + ".\n\n"
                        + "How many would you like?",
                ConversationState.ENTER_QUANTITY,
                false
        );
    }
}
