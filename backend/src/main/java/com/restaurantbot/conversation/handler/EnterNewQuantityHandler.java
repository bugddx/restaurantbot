package com.restaurantbot.conversation.handler;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.service.OrderItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterNewQuantityHandler implements ConversationHandler {

    private final OrderItemService orderItemService;

    @Override
    public ConversationState getState() {
        return ConversationState.ENTER_NEW_QUANTITY;
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

        OrderItem selectedOrderItem =
                conversation.getSelectedOrderItem();

        if (selectedOrderItem == null) {

            return new BotResponse(
                    """
                    ⚠️ No order item has been selected.

                    Please choose an item to change.
                    """,
                    ConversationState.CHANGE_QUANTITY,
                    false
            );
        }

        String input = message == null
                ? ""
                : message.trim();

        int quantity;

        try {
            quantity = Integer.parseInt(input);
        } catch (NumberFormatException e) {

            return new BotResponse(
                    """
                    ⚠️ Please enter a valid quantity.

                    Enter a number greater than zero.
                    """,
                    ConversationState.ENTER_NEW_QUANTITY,
                    false
            );
        }

        if (quantity <= 0) {

            return new BotResponse(
                    """
                    ⚠️ Quantity must be greater than zero.

                    Please enter the new quantity.
                    """,
                    ConversationState.ENTER_NEW_QUANTITY,
                    false
            );
        }

        Long restaurantId =
                conversation.getRestaurant().getId();

        Long orderId =
                conversation.getCurrentOrder().getId();

        Long orderItemId =
                selectedOrderItem.getId();

        UpdateOrderItemRequest request =
                new UpdateOrderItemRequest(
                        quantity,
                        selectedOrderItem.getNotes()
                );

        OrderItemResponse updated =
                orderItemService.update(
                        restaurantId,
                        orderId,
                        orderItemId,
                        request
                );

        conversation.setSelectedOrderItem(null);

        return new BotResponse(
                """
                ✅ Quantity updated.

                """
                        + updated.menuItemName()
                        + " × "
                        + updated.quantity()
                        + " = ₹"
                        + updated.subtotal()
                        + """

                        Your cart has been updated.
                        """,
                ConversationState.VIEW_CART,
                false
        );
    }
}
