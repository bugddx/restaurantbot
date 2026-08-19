package com.restaurantbot.conversation.handler;

import com.restaurantbot.conversation.dto.BotResponse;
import com.restaurantbot.conversation.entity.Conversation;
import com.restaurantbot.conversation.state.ConversationState;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.order.entity.Order;
import com.restaurantbot.orderitem.entity.OrderItem;
import com.restaurantbot.orderitem.repository.OrderItemRepository;
import com.restaurantbot.restaurant.entity.Restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ChangeQuantityHandlerTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    private ChangeQuantityHandler handler;

    private Conversation conversation;

    private Restaurant restaurant;

    private Order order;

    private MenuItem pizza;

    private MenuItem coke;

    private OrderItem pizzaOrderItem;

    private OrderItem cokeOrderItem;

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

        pizza = MenuItem.builder()
                .id(100L)
                .restaurant(restaurant)
                .name("Margherita")
                .price(new BigDecimal("299"))
                .available(true)
                .build();

        coke = MenuItem.builder()
                .id(200L)
                .restaurant(restaurant)
                .name("Coke")
                .price(new BigDecimal("80"))
                .available(true)
                .build();

        pizzaOrderItem = OrderItem.builder()
                .id(1000L)
                .order(order)
                .menuItem(pizza)
                .quantity(2)
                .unitPrice(new BigDecimal("299"))
                .subtotal(new BigDecimal("598"))
                .build();

        cokeOrderItem = OrderItem.builder()
                .id(1001L)
                .order(order)
                .menuItem(coke)
                .quantity(1)
                .unitPrice(new BigDecimal("80"))
                .subtotal(new BigDecimal("80"))
                .build();

        conversation = Conversation.builder()
                .id(10L)
                .phoneNumber("919876543210")
                .restaurant(restaurant)
                .currentOrder(order)
                .state(ConversationState.CHANGE_QUANTITY)
                .build();

        handler =
                new ChangeQuantityHandler(
                        orderItemRepository
                );
    }

    @Test
    void getState_shouldReturnChangeQuantity() {

        assertThat(handler.getState())
                .isEqualTo(
                        ConversationState.CHANGE_QUANTITY
                );
    }

    @Test
    void handle_shouldSelectFirstItem() {

        when(orderItemRepository.findByOrderId(500L))
                .thenReturn(
                        List.of(
                                pizzaOrderItem,
                                cokeOrderItem
                        )
                );

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isEqualTo(pizzaOrderItem);

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.ENTER_NEW_QUANTITY
                );

        assertThat(response.message())
                .contains("Margherita")
                .contains("2")
                .contains("new quantity");
    }

    @Test
    void handle_shouldSelectSecondItem() {

        when(orderItemRepository.findByOrderId(500L))
                .thenReturn(
                        List.of(
                                pizzaOrderItem,
                                cokeOrderItem
                        )
                );

        BotResponse response =
                handler.handle(
                        conversation,
                        "2"
                );

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isEqualTo(cokeOrderItem);

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.ENTER_NEW_QUANTITY
                );

        assertThat(response.message())
                .contains("Coke")
                .contains("1");
    }

    @Test
    void handle_shouldStayInChangeQuantity_whenInvalidNumber() {

        when(orderItemRepository.findByOrderId(500L))
                .thenReturn(
                        List.of(
                                pizzaOrderItem,
                                cokeOrderItem
                        )
                );

        BotResponse response =
                handler.handle(
                        conversation,
                        "5"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CHANGE_QUANTITY
                );

        assertThat(response.message())
                .contains("Invalid item number");

        assertThat(
                conversation.getSelectedOrderItem()
        )
                .isNull();
    }

    @Test
    void handle_shouldRedisplayItems_whenMessageIsNotNumber() {

        when(orderItemRepository.findByOrderId(500L))
                .thenReturn(
                        List.of(
                                pizzaOrderItem,
                                cokeOrderItem
                        )
                );

        BotResponse response =
                handler.handle(
                        conversation,
                        "abc"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CHANGE_QUANTITY
                );

        assertThat(response.message())
                .contains("Margherita")
                .contains("Coke")
                .contains("Reply with the item number");
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
                .isEqualTo(
                        ConversationState.VIEW_MENU
                );

        assertThat(response.message())
                .contains("cart is empty");
    }

    @Test
    void handle_shouldGoToViewMenu_whenOrderHasNoItems() {

        when(orderItemRepository.findByOrderId(500L))
                .thenReturn(List.of());

        BotResponse response =
                handler.handle(
                        conversation,
                        "1"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.VIEW_MENU
                );

        assertThat(response.message())
                .contains("cart is empty");
    }

    @Test
    void handle_shouldStayInChangeQuantity_whenSelectionIsZero() {

        when(orderItemRepository.findByOrderId(500L))
                .thenReturn(
                        List.of(
                                pizzaOrderItem,
                                cokeOrderItem
                        )
                );

        BotResponse response =
                handler.handle(
                        conversation,
                        "0"
                );

        assertThat(response.nextState())
                .isEqualTo(
                        ConversationState.CHANGE_QUANTITY
                );

        assertThat(response.message())
                .contains("Invalid item number");
    }
}
