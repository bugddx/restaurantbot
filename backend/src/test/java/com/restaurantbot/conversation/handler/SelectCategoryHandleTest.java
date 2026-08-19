package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.menucategory.service.MenuCategoryService;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectCategoryHandlerTest {

    @Mock
    private MenuCategoryService menuCategoryService;

    private SelectCategoryHandler handler;

    private Conversation conversation;
    private Restaurant restaurant;

    private MenuCategory pizzaCategory;
    private MenuCategory burgerCategory;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        pizzaCategory = MenuCategory.builder()
                .id(10L)
                .restaurant(restaurant)
                .name("Pizza")
                .displayOrder(1)
                .active(true)
                .build();

        burgerCategory = MenuCategory.builder()
                .id(20L)
                .restaurant(restaurant)
                .name("Burgers")
                .displayOrder(2)
                .active(true)
                .build();

        conversation = Conversation.builder()
                .id(100L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .state(ConversationState.SELECT_CATEGORY)
                .build();

        handler = new SelectCategoryHandler(
                menuCategoryService
        );
    }

    @Test
    void getState_shouldReturnSelectCategory() {

        assertThat(handler.getState())
                .isEqualTo(ConversationState.SELECT_CATEGORY);
    }

    @Test
    void handle_shouldSelectFirstCategory() {

        MenuCategoryResponse pizzaResponse =
                new MenuCategoryResponse(
                        10L,
                        1L,
                        "Pizza",
                        1,
                        true
                );

        MenuCategoryResponse burgerResponse =
                new MenuCategoryResponse(
                        20L,
                        1L,
                        "Burgers",
                        2,
                        true
                );

        Page<MenuCategoryResponse> page =
                new PageImpl<>(
                        List.of(
                                pizzaResponse,
                                burgerResponse
                        )
                );

        when(menuCategoryService.getAll(
                eq(1L),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(page);

        when(menuCategoryService.getEntityById(
                1L,
                10L
        )).thenReturn(pizzaCategory);

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(conversation.getSelectedCategory())
                .isEqualTo(pizzaCategory);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_CATEGORY);

        assertThat(response.message())
                .contains("Pizza");

        verify(menuCategoryService)
                .getEntityById(1L, 10L);
    }

    @Test
    void handle_shouldSelectSecondCategory() {

        MenuCategoryResponse pizzaResponse =
                new MenuCategoryResponse(
                        10L,
                        1L,
                        "Pizza",
                        1,
                        true
                );

        MenuCategoryResponse burgerResponse =
                new MenuCategoryResponse(
                        20L,
                        1L,
                        "Burgers",
                        2,
                        true
                );

        Page<MenuCategoryResponse> page =
                new PageImpl<>(
                        List.of(
                                pizzaResponse,
                                burgerResponse
                        )
                );

        when(menuCategoryService.getAll(
                eq(1L),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(page);

        when(menuCategoryService.getEntityById(
                1L,
                20L
        )).thenReturn(burgerCategory);

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(conversation.getSelectedCategory())
                .isEqualTo(burgerCategory);

        assertThat(response.nextState())
                .isEqualTo(ConversationState.VIEW_CATEGORY);

        assertThat(response.message())
                .contains("Burgers");

        verify(menuCategoryService)
                .getEntityById(1L, 20L);
    }

    @Test
    void handle_shouldStayOnSelectCategory_whenInputIsNotNumber() {

        BotResponse response =
                handler.handle(
                        conversation,
                        "abc"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.SELECT_CATEGORY);

        assertThat(response.message())
                .contains("valid category number");
    }

    @Test
    void handle_shouldStayOnSelectCategory_whenSelectionIsOutOfRange() {

        MenuCategoryResponse pizzaResponse =
                new MenuCategoryResponse(
                        10L,
                        1L,
                        "Pizza",
                        1,
                        true
                );

        Page<MenuCategoryResponse> page =
                new PageImpl<>(
                        List.of(pizzaResponse)
                );

        when(menuCategoryService.getAll(
                eq(1L),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(page);

        BotResponse response =
                handler.handle(
                        conversation,
                        "99"
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.SELECT_CATEGORY);

        assertThat(response.message())
                .contains("Invalid category selection");
    }

    @Test
    void handle_shouldStayOnSelectCategory_whenMessageIsNull() {

        BotResponse response =
                handler.handle(
                        conversation,
                        null
                );

        assertThat(response.nextState())
                .isEqualTo(ConversationState.SELECT_CATEGORY);

        assertThat(response.message())
                .contains("valid category number");
    }
}
