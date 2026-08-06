package com.restaurantbot.order.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.order.controller.OrderController;
import com.restaurantbot.order.dto.CreateOrderRequest;
import com.restaurantbot.order.dto.OrderResponse;
import com.restaurantbot.order.dto.UpdateOrderStatusRequest;
import com.restaurantbot.order.entity.OrderStatus;
import com.restaurantbot.order.service.OrderService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


    @WithMockUser
    @WebMvcTest(OrderController.class)
    @Import(GlobalExceptionHandler.class)
    class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private tools.jackson.databind.json.JsonMapper jsonMapper;

        @MockitoBean
        private OrderService orderService;

        @Test
        void create_shouldReturn201_whenOrderIsCreated()
            throws Exception {

            Long restaurantId = 1L;

            CreateOrderRequest request =
                new CreateOrderRequest(
                        10L,
                        "Extra cheese"
                        );

            OrderResponse response =
                new OrderResponse(
                        100L,
                        "ORD-ABC123",
                        restaurantId,
                        10L,
                        OrderStatus.OPEN,
                        new BigDecimal("500"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("500"),
                        "Extra cheese"
                        );

            given(orderService.create(
                        eq(restaurantId),
                        any(CreateOrderRequest.class)))
                .willReturn(response);

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/orders", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Order created successfully."))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.orderNumber")
                        .value("ORD-ABC123"));

            verify(orderService)
                .create(eq(restaurantId), any(CreateOrderRequest.class));
        }

        @Test
        void create_shouldReturn404_whenRestaurantNotFound()
            throws Exception {

            Long restaurantId = 1L;

            CreateOrderRequest request =
                new CreateOrderRequest(
                        10L,
                        "Extra cheese"
                        );

            given(orderService.create(
                        eq(restaurantId),
                        any(CreateOrderRequest.class)))
                .willThrow(
                        new ResourceNotFoundException("Restaurant not found")
                        );

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/orders", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void getById_shouldReturn200_whenOrderExists()
            throws Exception {

            Long restaurantId = 1L;
            Long orderId = 100L;

            OrderResponse response =
                new OrderResponse(
                        orderId,
                        "ORD-ABC123",
                        restaurantId,
                        10L,
                        OrderStatus.OPEN,
                        new BigDecimal("500"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("500"),
                        "Extra cheese"
                        );

            given(orderService.getById(
                        restaurantId,
                        orderId))
                .willReturn(response);

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/orders/{orderId}",
                        restaurantId,
                        orderId)
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.orderNumber")
                        .value("ORD-ABC123"));
        }

        @Test
        void getById_shouldReturn404_whenOrderDoesNotExist()
            throws Exception {

            given(orderService.getById(1L, 100L))
                .willThrow(
                        new ResourceNotFoundException("Order not found")
                        );

            mockMvc.perform(
                    get("/api/v1/restaurants/1/orders/100")
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void getAll_shouldReturn200_withOrders()
            throws Exception {

            OrderResponse response =
                new OrderResponse(
                        100L,
                        "ORD-ABC123",
                        1L,
                        10L,
                        OrderStatus.OPEN,
                        new BigDecimal("500"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("500"),
                        "Extra cheese"
                        );

            Page<OrderResponse> page =
                new PageImpl<>(List.of(response));

            given(orderService.getAll(
                        eq(1L),
                        any(Pageable.class)))
                .willReturn(page);

            mockMvc.perform(
                    get("/api/v1/restaurants/1/orders")
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.data.content[0].orderNumber")
                        .value("ORD-ABC123"));
        }

        @Test
        void updateStatus_shouldReturn200_whenStatusUpdated()
            throws Exception {

            Long restaurantId = 1L;
            Long orderId = 100L;

            UpdateOrderStatusRequest request =
                new UpdateOrderStatusRequest(
                        OrderStatus.PREPARING
                        );

            OrderResponse response =
                new OrderResponse(
                        orderId,
                        "ORD-ABC123",
                        restaurantId,
                        10L,
                        OrderStatus.PREPARING,
                        new BigDecimal("500"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("500"),
                        "Extra cheese"
                        );

            given(orderService.updateStatus(
                        eq(restaurantId),
                        eq(orderId),
                        any(UpdateOrderStatusRequest.class)))
                .willReturn(response);

            mockMvc.perform(
                    patch("/api/v1/restaurants/{restaurantId}/orders/{orderId}/status",
                        restaurantId,
                        orderId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Order status updated successfully."))
                .andExpect(jsonPath("$.data.status")
                        .value("PREPARING"));

            verify(orderService)
                .updateStatus(
                        eq(restaurantId),
                        eq(orderId),
                        any(UpdateOrderStatusRequest.class));
        }

        @Test
        void updateStatus_shouldReturn404_whenOrderDoesNotExist()
            throws Exception {

            UpdateOrderStatusRequest request =
                new UpdateOrderStatusRequest(
                        OrderStatus.PREPARING
                        );

            given(orderService.updateStatus(
                        eq(1L),
                        eq(100L),
                        any(UpdateOrderStatusRequest.class)))
                .willThrow(
                        new ResourceNotFoundException("Order not found")
                        );

            mockMvc.perform(
                    patch("/api/v1/restaurants/1/orders/100/status")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void delete_shouldReturn200_whenOrderDeleted()
            throws Exception {

            doNothing()
                .when(orderService)
                .delete(1L, 100L);

            mockMvc.perform(
                    delete("/api/v1/restaurants/1/orders/100")
                    .with(csrf())
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Order deleted successfully."));

            verify(orderService)
                .delete(1L, 100L);
        }

        @Test
        void delete_shouldReturn404_whenOrderDoesNotExist()
            throws Exception {

            doThrow(
                    new ResourceNotFoundException("Order not found")
                   )
                .when(orderService)
                .delete(1L, 100L);

            mockMvc.perform(
                    delete("/api/v1/restaurants/1/orders/100")
                    .with(csrf())
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void create_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

            CreateOrderRequest request =
                new CreateOrderRequest(
                        null,
                        "Extra cheese"
                        );

            mockMvc.perform(
                    post("/api/v1/restaurants/1/orders")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isBadRequest());

            verify(orderService, never())
                .create(anyLong(), any());
        }
    }
