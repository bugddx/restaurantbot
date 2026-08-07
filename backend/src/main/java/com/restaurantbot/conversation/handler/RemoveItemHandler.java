package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RemoveItemHandler implements ConversationHandler {

    private final OrderItemRepository orderItemRepository;

    @Override
    public ConversationState getState() {
        return ConversationState.REMOVE_ITEM;
    }

    @Override
    public BotResponse handle(
            Conversation conversation,
            String message) {

        if (conversation.getCurrentOrder() == null) {

            return new BotResponse(
                    """
                    🛒 Your cart is empty.

                    Please choose an item from the menu.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        Long orderId =
                conversation.getCurrentOrder().getId();

        List<OrderItem> items =
                orderItemRepository.findByOrderId(orderId);

        if (items.isEmpty()) {

            return new BotResponse(
                    """
                    🛒 Your cart is empty.

                    Please choose an item from the menu.
                    """,
                    ConversationState.VIEW_MENU,
                    false
            );
        }

        String input = message == null
                ? ""
                : message.trim();

        int selection;

        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {

            return new BotResponse(
                    buildItemListMessage(items),
                    ConversationState.REMOVE_ITEM,
                    false
            );
        }

        if (selection < 1 || selection > items.size()) {

            return new BotResponse(
                    """
                    ⚠️ Invalid item number.

                    Please choose one of the items
                    shown above.
                    """,
                    ConversationState.REMOVE_ITEM,
                    false
            );
        }

        OrderItem selectedItem =
                items.get(selection - 1);

        conversation.setSelectedOrderItem(
                selectedItem
        );

        return new BotResponse(
                """
                🗑️ Remove """
                        + selectedItem.getMenuItem().getName()
                        + " × "
                        + selectedItem.getQuantity()
                        + " from your cart?\n\n"
                        + "1️⃣ Yes\n"
                        + "2️⃣ No",
                ConversationState.CONFIRM_REMOVE_ITEM,
                false
        );
    }

    private String buildItemListMessage(
            List<OrderItem> items) {

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "🗑️ Which item would you like to remove?\n\n"
        );

        for (int i = 0; i < items.size(); i++) {

            OrderItem item = items.get(i);

            builder
                    .append(i + 1)
                    .append("️⃣ ")
                    .append(item.getMenuItem().getName())
                    .append(" × ")
                    .append(item.getQuantity())
                    .append("\n");
        }

        builder.append(
                "\nReply with the item number."
        );

        return builder.toString();
    }
}
