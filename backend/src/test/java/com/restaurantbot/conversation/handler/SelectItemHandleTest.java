package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menuitem.service.MenuItemService;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectItemHandlerTest {

    @Mock
    private MenuItemService menuItemService;

    private SelectItemHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private MenuCategory category;

    private MenuItem pizza;

    private MenuItem burger;

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
                .name("Main Course")
                .displayOrder(1)
                .active(true)
                .build();

        pizza = MenuItem.builder()
                .id(100L)
                .restaurant(restaurant)
                .category(category)
                .name("Margherita")
                .price(new BigDecimal("299"))
                .available(true)
                .displayOrder(1)
                .build();

        burger = MenuItem.builder()
                .id(200L)
                .restaurant(restaurant)
                .category(category)
                .name("Cheese Burger")
                .price(new BigDecimal("349"))
                .available(true)
                .displayOrder(2)
                .build();

        conversation = Conversation.builder()
                .id(1000L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .selectedCategory(category)
                .state(ConversationState.SELECT_ITEM)
                .build();

        handler = new SelectItemHandler(
                menuItemService
        );
    }

    @Test
    void getState_shouldReturnSelectItem() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.SELECT_ITEM);
    }

    @Test
    void handle_shouldSelectFirstItem() {

        MenuItemResponse pizzaResponse =
                new MenuItemResponse(
                        100L,
                        1L,
                        10L,
                        "Main Course",
                        "Margherita",
                        "Classic pizza",
                        new BigDecimal("299"),
                        null,
                        15,
                        true,
                        false,
                        true,
                        1
                );

        MenuItemResponse burgerResponse =
                new MenuItemResponse(
                        200L,
                        1L,
                        10L,
                        "Main Course",
                        "Cheese Burger",
                        "Classic burger",
                        new BigDecimal("349"),
                        null,
                        20,
                        false,
                        false,
                        true,
                        2
                );

        Page<MenuItemResponse> page =
                new PageImpl<>(
                        List.of(
                                pizzaResponse,
                                burgerResponse
                        )
                );

        when(menuItemService.getByCategory(
                eq(1L),
                eq(10L),
                any()
        )).thenReturn(page);

        when(menuItemService.getEntityById(
                1L,
                100L
        )).thenReturn(pizza);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(conversation.getSelectedItem())
                .isEqualTo(pizza);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);

        assertThat(response.message())
                .contains("Margherita")
                .contains("How many");

        verify(menuItemService)
                .getEntityById(1L, 100L);
    }

    @Test
    void handle_shouldSelectSecondItem() {

        MenuItemResponse pizzaResponse =
                new MenuItemResponse(
                        100L,
                        1L,
                        10L,
                        "Main Course",
                        "Margherita",
                        "Classic pizza",
                        new BigDecimal("299"),
                        null,
                        15,
                        true,
                        false,
                        true,
                        1
                );

        MenuItemResponse burgerResponse =
                new MenuItemResponse(
                        200L,
                        1L,
                        10L,
                        "Main Course",
                        "Cheese Burger",
                        "Classic burger",
                        new BigDecimal("349"),
                        null,
                        20,
                        false,
                        false,
                        true,
                        2
                );

        Page<MenuItemResponse> page =
                new PageImpl<>(
                        List.of(
                                pizzaResponse,
                                burgerResponse
                        )
                );

        when(menuItemService.getByCategory(
                eq(1L),
                eq(10L),
                any()
        )).thenReturn(page);

        when(menuItemService.getEntityById(
                1L,
                200L
        )).thenReturn(burger);

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(conversation.getSelectedItem())
                .isEqualTo(burger);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.ENTER_QUANTITY);

        assertThat(response.message())
                .contains("Cheese Burger")
                .contains("How many");

        verify(menuItemService)
                .getEntityById(1L, 200L);
    }

    @Test
    void handle_shouldStayOnSelectItem_whenInputIsNotNumber() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "abc"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.SELECT_ITEM);

        assertThat(response.message())
                .contains("valid item number");
    }

    @Test
    void handle_shouldStayOnSelectItem_whenSelectionIsOutOfRange() {

        MenuItemResponse pizzaResponse =
                new MenuItemResponse(
                        100L,
                        1L,
                        10L,
                        "Main Course",
                        "Margherita",
                        "Classic pizza",
                        new BigDecimal("299"),
                        null,
                        15,
                        true,
                        false,
                        true,
                        1
                );

        Page<MenuItemResponse> page =
                new PageImpl<>(
                        List.of(pizzaResponse)
                );

        when(menuItemService.getByCategory(
                eq(1L),
                eq(10L),
                any()
        )).thenReturn(page);

        BotResponse response =
                handler.handle(
                        conversation,
                        "99"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.SELECT_ITEM);

        assertThat(response.message())
                .contains("Invalid item selection");
    }

    @Test
    void handle_shouldReturnToMenu_whenCategoryIsNotSelected() {

        conversation.setSelectedCategory(null);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_MENU);

        assertThat(response.message())
                .contains("No category");
    }

    @Test
    void handle_shouldStayOnSelectItem_whenMessageIsNull() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.SELECT_ITEM);

        assertThat(response.message())
                .contains("valid item number");
    }
}
