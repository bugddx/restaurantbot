package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartActionHandlerTest {

    private CartActionHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        order = Order.builder()
                .id(100L)
                .restaurant(restaurant)
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .state(ConversationState.CART_ACTION)
                .build();

        handler = new CartActionHandler();
    }

    @Test
    void getState_shouldReturnCartAction() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.CART_ACTION);
    }

    @Test
    void handle_shouldGoToViewMenu_whenAddMoreSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("add more items");
    }

    @Test
    void handle_shouldGoToChangeQuantity_whenChangeQuantitySelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.CHANGE_QUANTITY);

        assertThat(response.message())
                .contains("change the quantity");
    }

    @Test
    void handle_shouldGoToRemoveItem_whenRemoveSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "3"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.REMOVE_ITEM);

        assertThat(response.message())
                .contains("remove");
    }

    @Test
    void handle_shouldGoToConfirmOrder_whenPlaceOrderSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "4"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.CONFIRM_ORDER);

        assertThat(response.message())
                .contains("confirm");
    }

    @Test
    void handle_shouldCancelConversationOrder_whenCancelSelected() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "5"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.MAIN_MENU);

        assertThat(response.message())
                .contains("cancelled");

        assertThat(conversation.getCurrentOrder())
                .isNull();

        assertThat(conversation.getSelectedItem())
                .isNull();

        assertThat(conversation.getSelectedCategory())
                .isNull();
    }

    @Test
    void handle_shouldStayInCartAction_whenInvalidOption() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "9"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.CART_ACTION);

        assertThat(response.message())
                .contains("Invalid option");
    }

    @Test
    void handle_shouldStayInCartAction_whenMessageIsInvalidText() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "hello"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.CART_ACTION);

        assertThat(response.message())
                .contains("Invalid option");
    }

    @Test
    void handle_shouldGoToViewMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("cart is empty");
    }

    @Test
    void handle_shouldGoToViewMenu_whenMessageIsNullAndNoOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("cart is empty");
    }
}
