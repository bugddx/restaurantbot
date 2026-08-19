package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.service.OrderItemService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnterQuantityHandlerTest {

    @Mock
    private OrderItemService orderItemService;

    private EnterQuantityHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private MenuCategory category;

    private MenuItem item;

    private Order order;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        category = MenuCategory.builder()
                .id(10L)
                .restaurant(restaurant)
                .name("Pizza")
                .build();

        item = MenuItem.builder()
                .id(100L)
                .restaurant(restaurant)
                .category(category)
                .name("Margherita")
                .price(new BigDecimal("299"))
                .available(true)
                .build();

        order = Order.builder()
                .id(500L)
                .restaurant(restaurant)
                .build();

        conversation = Conversation.builder()
                .id(1000L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .selectedCategory(category)
                .selectedItem(item)
                .state(ConversationState.ENTER_QUANTITY)
                .build();

        handler = new EnterQuantityHandler(
                orderItemService
        );
    }

    @Test
    void getState_shouldReturnEnterQuantity() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);
    }

    @Test
    void handle_shouldAddItemToOrder() {

        when(orderItemService.create(
                eq(1L),
                eq(500L),
                any()
        )).thenReturn(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_CART);

        assertThat(response.message())
                .contains("2")
                .contains("Margherita")
                .contains("Added");

        verify(orderItemService)
                .create(
                        eq(1L),
                        eq(500L),
                        any()
                );
    }

    @Test
    void handle_shouldStayOnEnterQuantity_whenQuantityIsNotNumber() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "abc"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);

        assertThat(response.message())
                .contains("valid quantity");

        verify(orderItemService, never())
                .create(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void handle_shouldStayOnEnterQuantity_whenQuantityIsZero() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "0"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);

        assertThat(response.message())
                .contains("at least 1");

        verify(orderItemService, never())
                .create(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void handle_shouldStayOnEnterQuantity_whenQuantityIsNegative() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "-2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);

        verify(orderItemService, never())
                .create(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void handle_shouldStayOnEnterQuantity_whenMessageIsNull() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);

        assertThat(response.message())
                .contains("valid quantity");

        verify(orderItemService, never())
                .create(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void handle_shouldReturnToMainMenu_whenNoCurrentOrder() {

        conversation.setCurrentOrder(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.MAIN_MENU);

        assertThat(response.message())
                .contains("No active order");

        verify(orderItemService, never())
                .create(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void handle_shouldReturnToViewCategory_whenNoSelectedItem() {

        conversation.setSelectedItem(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_CATEGORY);

        assertThat(response.message())
                .contains("No item");

        verify(orderItemService, never())
                .create(
                        any(),
                        any(),
                        any()
                );
    }
}
