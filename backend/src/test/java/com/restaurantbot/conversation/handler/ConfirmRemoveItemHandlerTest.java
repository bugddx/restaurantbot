package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.service.OrderItemService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ConfirmRemoveItemHandlerTest {

    @Mock
    private OrderItemService orderItemService;

    private ConfirmRemoveItemHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;

    private MenuItem menuItem;

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        order = Order.builder()
                .id(500L)
                .restaurant(restaurant)
                .build();

        menuItem = MenuItem.builder()
                .id(100L)
                .restaurant(restaurant)
                .name("Margherita")
                .price(new BigDecimal("299"))
                .available(true)
                .build();

        orderItem = OrderItem.builder()
                .id(1000L)
                .order(order)
                .menuItem(menuItem)
                .quantity(2)
                .unitPrice(new BigDecimal("299"))
                .subtotal(new BigDecimal("598"))
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .selectedOrderItem(orderItem)
                .state(ConversationState.CONFIRM_REMOVE_ITEM)
                .build();

        handler =
                new ConfirmRemoveItemHandler(
                        orderItemService
                );
    }

    @Test
    void getState_shouldReturnConfirmRemoveItem() {

        assertThat(handler.getState())
                .isEqualTo(
                        ConversationState.CONFIRM_REMOVE_ITEM
                );
    }

    @Test
    void handle_shouldDeleteItem_whenConfirmed() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(orderItemService)
                .delete(
                        1L,
                        500L,
                        1000L
                );

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isNull();

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_CART
                );

        assertThat(response.message())
                .contains("Margherita")
                .contains("removed")
                .contains("updated");
    }

    @Test
    void handle_shouldNotDelete_whenCancelled() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        verify(orderItemService, never())
                .delete(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isNull();

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_CART
                );

        assertThat(response.message())
                .contains("not removed")
                .contains("unchanged");
    }

    @Test
    void handle_shouldStayInConfirmation_whenInvalidChoice() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "5"
                );

        verify(orderItemService, never())
                .delete(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isEqualTo(orderItem);

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CONFIRM_REMOVE_ITEM
                );

        assertThat(response.message())
                .contains("Invalid choice")
                .contains("1")
                .contains("2");
    }

    @Test
    void handle_shouldGoToViewMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(orderItemService, never())
                .delete(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_MENU
                );

        assertThat(response.message())
                .contains("cart is empty");
    }

    @Test
    void handle_shouldGoToRemoveItem_whenNoSelectedItem() {

        conversation.setSelectedOrderItem(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        verify(orderItemService, never())
                .delete(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.REMOVE_ITEM
                );

        assertThat(response.message())
                .contains("No item has been selected");
    }

    @Test
    void handle_shouldHandleNullMessage() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        verify(orderItemService, never())
                .delete(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CONFIRM_REMOVE_ITEM
                );

        assertThat(response.message())
                .contains("Invalid choice");
    }
}
