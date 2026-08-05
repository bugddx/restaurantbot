package com.restaurantbot.menucategory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menucategory.dto.CreateMenuCategoryRequest;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.dto.UpdateMenuCategoryRequest;
import com.restaurantbot.menucategory.service.MenuCategoryService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @WithMockUser
    @WebMvcTest(MenuCategoryController.class)
    @Import(GlobalExceptionHandler.class)
    class MenuCategoryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private tools.jackson.databind.json.JsonMapper jsonMapper;
        @MockitoBean
        private MenuCategoryService menuCategoryService;

        private final Long restaurantId = 1L;
        private final Long categoryId = 1L;

        private final CreateMenuCategoryRequest createRequest =
            new CreateMenuCategoryRequest(
                    "Pizza",
                    1
                    );

        private final UpdateMenuCategoryRequest updateRequest =
            new UpdateMenuCategoryRequest(
                    "Premium Pizza",
                    2,
                    true
                    );

        private final MenuCategoryResponse response =
            new MenuCategoryResponse(
                    1L,
                    1L,
                    "Pizza",
                    1,
                    true
                    );

        @Test
        void create_shouldReturn201_whenCategoryIsCreated()
            throws Exception {

            given(menuCategoryService.create(
                        eq(restaurantId),
                        any(CreateMenuCategoryRequest.class)))
                .willReturn(response);

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-categories", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(createRequest))
                    )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu category created successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.restaurantId").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza"))
                .andExpect(jsonPath("$.data.displayOrder").value(1))
                .andExpect(jsonPath("$.data.active").value(true));

            verify(menuCategoryService)
                .create(eq(restaurantId), any(CreateMenuCategoryRequest.class));
        }

        @Test
        void create_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

            given(menuCategoryService.create(
                        eq(restaurantId),
                        any(CreateMenuCategoryRequest.class)))
                .willThrow(new ResourceNotFoundException("Restaurant not found"));

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-categories", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(createRequest))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void create_shouldReturn409_whenCategoryAlreadyExists()
            throws Exception {

            given(menuCategoryService.create(
                        eq(restaurantId),
                        any(CreateMenuCategoryRequest.class)))
                .willThrow(new ResourceAlreadyExistsException(
                            "Category already exists for this restaurant"));

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-categories", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(createRequest))
                    )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void getById_shouldReturn200_whenCategoryExists()
            throws Exception {

            given(menuCategoryService.getById(
                        restaurantId,
                        categoryId))
                .willReturn(response);

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza"));
        }

        @Test
        void getById_shouldReturn404_whenCategoryDoesNotExist()
            throws Exception {

            given(menuCategoryService.getById(
                        restaurantId,
                        categoryId))
                .willThrow(new ResourceNotFoundException(
                            "Menu category not found"));

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void getAll_shouldReturn200_withCategories()
            throws Exception {

            given(menuCategoryService.getAll(
                        eq(restaurantId),
                        any(PageRequest.class)))
                .willReturn(
                        new PageImpl<>(
                            List.of(response),
                            PageRequest.of(0, 20),
                            1
                            )
                        );

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-categories",
                        restaurantId)
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu categories retrieved successfully."))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("Pizza"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void getAll_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

            given(menuCategoryService.getAll(
                        eq(restaurantId),
                        any(PageRequest.class)))
                .willThrow(
                        new ResourceNotFoundException(
                            "Restaurant not found"
                            )
                        );

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/menu-categories",
                        restaurantId)
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void update_shouldReturn200_whenCategoryIsUpdated()
            throws Exception {

            MenuCategoryResponse updatedResponse =
                new MenuCategoryResponse(
                        categoryId,
                        restaurantId,
                        "Premium Pizza",
                        2,
                        true
                        );

            given(menuCategoryService.update(
                        eq(restaurantId),
                        eq(categoryId),
                        any(UpdateMenuCategoryRequest.class)))
                .willReturn(updatedResponse);

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(updateRequest))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu category updated successfully."))
                .andExpect(jsonPath("$.data.name")
                        .value("Premium Pizza"))
                .andExpect(jsonPath("$.data.displayOrder")
                        .value(2));
        }

        @Test
        void update_shouldReturn404_whenCategoryDoesNotExist()
            throws Exception {

            given(menuCategoryService.update(
                        eq(restaurantId),
                        eq(categoryId),
                        any(UpdateMenuCategoryRequest.class)))
                .willThrow(
                        new ResourceNotFoundException(
                            "Menu category not found"
                            )
                        );

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(updateRequest))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void update_shouldReturn409_whenCategoryAlreadyExists()
            throws Exception {

            given(menuCategoryService.update(
                        eq(restaurantId),
                        eq(categoryId),
                        any(UpdateMenuCategoryRequest.class)))
                .willThrow(
                        new ResourceAlreadyExistsException(
                            "Category already exists for this restaurant"
                            )
                        );

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(updateRequest))
                    )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void delete_shouldReturn200_whenCategoryIsDeleted()
            throws Exception {

            doNothing()
                .when(menuCategoryService)
                .delete(restaurantId, categoryId);

            mockMvc.perform(
                    delete("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    .with(csrf())
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Menu category deleted successfully."));

            verify(menuCategoryService)
                .delete(restaurantId, categoryId);
        }

        @Test
        void delete_shouldReturn404_whenCategoryDoesNotExist()
            throws Exception {

            doThrow(new ResourceNotFoundException("Menu category not found"))
                .when(menuCategoryService)
                .delete(restaurantId, categoryId);

            mockMvc.perform(
                    delete("/api/v1/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId)
                    .with(csrf())
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void create_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

            CreateMenuCategoryRequest invalidRequest =
                new CreateMenuCategoryRequest(
                        "",
                        1
                        );

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/menu-categories",
                        restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(invalidRequest))
                    )
                .andExpect(status().isBadRequest());

            verify(menuCategoryService, never())
                .create(anyLong(), any(CreateMenuCategoryRequest.class));
        }
    }
