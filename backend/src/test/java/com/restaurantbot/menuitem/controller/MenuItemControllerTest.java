package com.restaurantbot.menuitem.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menuitem.dto.CreateMenuItemRequest;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.dto.UpdateMenuItemRequest;
import com.restaurantbot.menuitem.service.MenuItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
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
    @WebMvcTest(MenuItemController.class)
    @Import(GlobalExceptionHandler.class)
    class MenuItemControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private tools.jackson.databind.json.JsonMapper jsonMapper;

        @MockitoBean
        private MenuItemService menuItemService;

        private MenuItemResponse response() {

            return new MenuItemResponse(
                    1L,
                    1L,
                    10L,
                    "Pizza",
                    "Margherita",
                    "Classic pizza",
                    new BigDecimal("299"),
                    null,
                    20,
                    true,
                    false,
                    true,
                    1
                    );
        }

        @Test
        void create_shouldReturn201_whenMenuItemCreated()
            throws Exception {

            CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        10L,
                        "Margherita",
                        "Classic pizza",
                        new BigDecimal("299"),
                        null,
                        20,
                        true,
                        false,
                        true,
                        1
                        );

            given(menuItemService.create(
                        eq(1L),
                        any(CreateMenuItemRequest.class)))
                .willReturn(response());

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-items",1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu item created successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Margherita"));

            verify(menuItemService)
                .create(eq(1L), any(CreateMenuItemRequest.class));
        }

        @Test
        void create_shouldReturn409_whenDuplicateExists()
            throws Exception {

            CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        10L,
                        "Margherita",
                        "Classic pizza",
                        new BigDecimal("299"),
                        null,
                        20,
                        true,
                        false,
                        true,
                        1
                        );

            given(menuItemService.create(
                        eq(1L),
                        any(CreateMenuItemRequest.class)))
                .willThrow(
                        new ResourceAlreadyExistsException(
                            "Menu item already exists in this category"));

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-items",1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void create_shouldReturn404_whenRestaurantOrCategoryNotFound()
            throws Exception {

            CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        10L,
                        "Margherita",
                        "Classic pizza",
                        new BigDecimal("299"),
                        null,
                        20,
                        true,
                        false,
                        true,
                        1
                        );

            given(menuItemService.create(
                        eq(1L),
                        any(CreateMenuItemRequest.class)))
                .willThrow(
                        new ResourceNotFoundException(
                            "Restaurant not found"));

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-items",1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void create_shouldReturn400_whenRequestInvalid()
            throws Exception {

            CreateMenuItemRequest request =
                new CreateMenuItemRequest(
                        null,
                        "",
                        "",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                        );

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-items",1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isBadRequest());

            verify(menuItemService, never())
                .create(anyLong(), any());
        }

        @Test
        void getById_shouldReturn200_whenMenuItemExists()
            throws Exception {

            given(menuItemService.getById(1L, 1L))
                .willReturn(response());

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-items/{itemId}",
                        1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu item retrieved successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Margherita"));
        }

        @Test
        void getAll_shouldReturn200()
            throws Exception {

            PageImpl<MenuItemResponse> page =
                new PageImpl<>(List.of(response()));

            given(menuItemService.getAll(eq(1L), any()))
                .willReturn(page);

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-items", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("Margherita"));
        }

        @Test
        void getByCategory_shouldReturn200()
            throws Exception {

            PageImpl<MenuItemResponse> page =
                new PageImpl<>(List.of(response()));

            given(menuItemService.getByCategory(
                        eq(1L),
                        eq(10L),
                        any()))
                .willReturn(page);

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-items/category/{categoryId}",
                        1L,
                        10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1));
        }

        @Test
        void getByCategory_shouldReturn404_whenCategoryDoesNotExist()
            throws Exception {

            given(menuItemService.getByCategory(
                        eq(1L),
                        eq(10L),
                        any()))
                .willThrow(
                        new ResourceNotFoundException(
                            "Menu category not found"));

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-items/category/{categoryId}",
                        1L,
                        10L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void update_shouldReturn200_whenMenuItemUpdated()
            throws Exception {

            UpdateMenuItemRequest request =
                new UpdateMenuItemRequest(
                        10L,
                        "Farmhouse",
                        "Loaded pizza",
                        new BigDecimal("399"),
                        null,
                        20,
                        true,
                        false,
                        true,
                        2
                        );

            given(menuItemService.update(
                        eq(1L),
                        eq(1L),
                        any(UpdateMenuItemRequest.class)))
                .willReturn(response());

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/menu-items/{itemId}",
                        1L,
                        1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu item updated successfully."));
        }

        @Test
        void update_shouldReturn409_whenDuplicateExists()
            throws Exception {

            UpdateMenuItemRequest request =
                new UpdateMenuItemRequest(
                        10L,
                        "Farmhouse",
                        "",
                        new BigDecimal("399"),
                        null,
                        20,
                        true,
                        false,
                        true,
                        2
                        );

            given(menuItemService.update(
                        eq(1L),
                        eq(1L),
                        any(UpdateMenuItemRequest.class)))
                .willThrow(
                        new ResourceAlreadyExistsException(
                            "Menu item already exists in this category"));

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/menu-items/{itemId}",
                        1L,
                        1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
        }

        @Test
        void update_shouldReturn404_whenMenuItemDoesNotExist()
            throws Exception {

            UpdateMenuItemRequest request =
                new UpdateMenuItemRequest(
                        10L,
                        "Farmhouse",
                        "",
                        new BigDecimal("399"),
                        null,
                        20,
                        true,
                        false,
                        true,
                        2
                        );

            given(menuItemService.update(
                        eq(1L),
                        eq(1L),
                        any(UpdateMenuItemRequest.class)))
                .willThrow(
                        new ResourceNotFoundException(
                            "Menu item not found"));

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/menu-items/{itemId}",
                        1L,
                        1L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        void delete_shouldReturn200_whenMenuItemDeleted()
            throws Exception {

            doNothing()
                .when(menuItemService)
                .delete(1L, 1L);

            mockMvc.perform(
                    delete("/api/v1/restaurants/{restaurantId}/menu-items/{itemId}",
                        1L,
                        1L)
                    .with(csrf()))
                .andExpect(status().isOk());

            verify(menuItemService).delete(1L, 1L);
        }

        @Test
        void delete_shouldReturn404_whenMenuItemDoesNotExist()
            throws Exception {

            doThrow(new ResourceNotFoundException("Menu item not found"))
                .when(menuItemService)
                .delete(1L, 1L);

            mockMvc.perform(
                    delete("/api/v1/restaurants/{restaurantId}/menu-items/{itemId}",
                        1L,
                        1L)
                    .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

    }
