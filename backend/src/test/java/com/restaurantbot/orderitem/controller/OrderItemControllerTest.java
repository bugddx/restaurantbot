package com.restaurantbot.orderitem.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.orderitem.dto.CreateOrderItemRequest;
import com.restaurantbot.orderitem.dto.OrderItemResponse;
import com.restaurantbot.orderitem.dto.UpdateOrderItemRequest;
import com.restaurantbot.orderitem.service.OrderItemService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


    @WithMockUser
    @WebMvcTest(OrderItemController.class)
    @Import(GlobalExceptionHandler.class)
    class OrderItemControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private tools.jackson.databind.json.JsonMapper jsonMapper;

        @MockitoBean
        private OrderItemService service;

        @Test
        void create_shouldReturn201_whenOrderItemCreated()
            throws Exception {

            CreateOrderItemRequest request =
                new CreateOrderItemRequest(
                        100L,
                        2,
                        "Extra cheese"
                        );

            OrderItemResponse response =
                new OrderItemResponse(
                        1L,
                        10L,
                        100L,
                        "Margherita Pizza",
                        2,
                        new BigDecimal("299"),
                        new BigDecimal("598"),
                        "Extra cheese"
                        );

            given(service.create(
                        eq(1L),
                        eq(10L),
                        any(CreateOrderItemRequest.class)))
                .willReturn(response);

            mockMvc.perform(
                    post("/api/v1/restaurants/1/orders/10/items")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

            verify(service)
                .create(
                        eq(1L),
                        eq(10L),
                        any(CreateOrderItemRequest.class));
        }

        @Test
        void create_shouldReturn400_whenRequestInvalid()
            throws Exception {

            CreateOrderItemRequest request =
                new CreateOrderItemRequest(
                        null,
                        0,
                        null
                        );

            mockMvc.perform(
                    post("/api/v1/restaurants/1/orders/10/items")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isBadRequest());

            verify(service, never())
                .create(anyLong(), anyLong(), any());
        }

        @Test
        void getById_shouldReturn200()
            throws Exception {

            OrderItemResponse response =
                new OrderItemResponse(
                        1L,
                        10L,
                        100L,
                        "Margherita Pizza",
                        2,
                        new BigDecimal("299"),
                        new BigDecimal("598"),
                        "Extra cheese"
                        );

            given(service.getById(
                        1L,
                        10L,
                        1L))
                .willReturn(response);

            mockMvc.perform(
                    get("/api/v1/restaurants/1/orders/10/items/1")
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        void getById_shouldReturn404()
            throws Exception {

            given(service.getById(
                        1L,
                        10L,
                        1L))
                .willThrow(
                        new ResourceNotFoundException("Order item not found")
                        );

            mockMvc.perform(
                    get("/api/v1/restaurants/1/orders/10/items/1")
                    )
                .andExpect(status().isNotFound());
        }

        @Test
        void getAll_shouldReturn200()
            throws Exception {

            OrderItemResponse response =
                new OrderItemResponse(
                        1L,
                        10L,
                        100L,
                        "Margherita Pizza",
                        2,
                        new BigDecimal("299"),
                        new BigDecimal("598"),
                        "Extra cheese"
                        );

            given(service.getAll(
                        1L,
                        10L))
                .willReturn(List.of(response));

            mockMvc.perform(
                    get("/api/v1/restaurants/1/orders/10/items")
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
        }

        @Test
        void update_shouldReturn200()
            throws Exception {

            UpdateOrderItemRequest request =
                new UpdateOrderItemRequest(
                        3,
                        "No onions"
                        );

            OrderItemResponse response =
                new OrderItemResponse(
                        1L,
                        10L,
                        100L,
                        "Margherita Pizza",
                        3,
                        new BigDecimal("299"),
                        new BigDecimal("897"),
                        "No onions"
                        );

            given(service.update(
                        eq(1L),
                        eq(10L),
                        eq(1L),
                        any(UpdateOrderItemRequest.class)))
                .willReturn(response);

            mockMvc.perform(
                    put("/api/v1/restaurants/1/orders/10/items/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(3));

            verify(service)
                .update(
                        eq(1L),
                        eq(10L),
                        eq(1L),
                        any(UpdateOrderItemRequest.class));
        }

        @Test
        void update_shouldReturn404()
            throws Exception {

            UpdateOrderItemRequest request =
                new UpdateOrderItemRequest(
                        3,
                        "No onions"
                        );

            given(service.update(
                        eq(1L),
                        eq(10L),
                        eq(1L),
                        any(UpdateOrderItemRequest.class)))
                .willThrow(
                        new ResourceNotFoundException("Order item not found")
                        );

            mockMvc.perform(
                    put("/api/v1/restaurants/1/orders/10/items/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isNotFound());
        }

        @Test
        void delete_shouldReturn200()
            throws Exception {

            doNothing()
                .when(service)
                .delete(
                        1L,
                        10L,
                        1L
                       );

            mockMvc.perform(
                    delete("/api/v1/restaurants/1/orders/10/items/1")
                    .with(csrf())
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(service)
                .delete(
                        1L,
                        10L,
                        1L
                       );
        }

        @Test
        void delete_shouldReturn404()
            throws Exception {

            doThrow(
                    new ResourceNotFoundException("Order item not found")
                   )
                .when(service)
                .delete(
                        1L,
                        10L,
                        1L
                       );

            mockMvc.perform(
                    delete("/api/v1/restaurants/1/orders/10/items/1")
                    .with(csrf())
                    )
                .andExpect(status().isNotFound());
        }
    }
