package com.restaurantbot.restauranttable.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.restauranttable.dto.CreateRestaurantTableRequest;
import com.restaurantbot.restauranttable.dto.RestaurantTableResponse;
import com.restaurantbot.restauranttable.dto.UpdateRestaurantTableRequest;
import com.restaurantbot.restauranttable.entity.TableStatus;
import com.restaurantbot.restauranttable.service.RestaurantTableService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @WithMockUser
    @WebMvcTest(RestaurantTableController.class)
    @Import(GlobalExceptionHandler.class)
    class RestaurantTableControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private tools.jackson.databind.json.JsonMapper jsonMapper;

        @MockitoBean
        private RestaurantTableService restaurantTableService;

        @Test
        void create_shouldReturn201_whenTableIsCreated()
            throws Exception {

            Long restaurantId = 1L;

            CreateRestaurantTableRequest request =
                new CreateRestaurantTableRequest("T1");

            RestaurantTableResponse response =
                new RestaurantTableResponse(
                        10L,
                        restaurantId,
                        "T1",
                        UUID.randomUUID(),
                        TableStatus.AVAILABLE,
                        "https://example.com"
                        );

            given(
                    restaurantTableService.create(
                        anyLong(),
                        any(CreateRestaurantTableRequest.class)
                        )
                 ).willReturn(response);

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/tables", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant table created successfully."))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.restaurantId").value(1))
                .andExpect(jsonPath("$.data.tableNumber").value("T1"))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));

            verify(restaurantTableService)
                .create(anyLong(), any(CreateRestaurantTableRequest.class));
        }

        @Test
        void create_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

            Long restaurantId = 1L;

            CreateRestaurantTableRequest request =
                new CreateRestaurantTableRequest("T1");

            given(
                    restaurantTableService.create(
                        anyLong(),
                        any(CreateRestaurantTableRequest.class)
                        )
                 ).willThrow(
                     new ResourceNotFoundException("Restaurant not found")
                     );

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/tables", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant not found"));
        }

        @Test
        void create_shouldReturn409_whenTableAlreadyExists()
            throws Exception {

            Long restaurantId = 1L;

            CreateRestaurantTableRequest request =
                new CreateRestaurantTableRequest("T1");

            given(
                    restaurantTableService.create(
                        anyLong(),
                        any(CreateRestaurantTableRequest.class)
                        )
                 ).willThrow(
                     new ResourceAlreadyExistsException(
                         "Table number already exists for this restaurant"
                         )
                     );

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/tables", restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Table number already exists for this restaurant"));
        }

        @Test
        void getById_shouldReturn200_whenTableExists()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 10L;

            RestaurantTableResponse response =
                new RestaurantTableResponse(
                        tableId,
                        restaurantId,
                        "T1",
                        UUID.randomUUID(),
                        TableStatus.AVAILABLE,
                        "https://example.com"
                        );

            given(
                    restaurantTableService.getById(
                        restaurantId,
                        tableId
                        )
                 ).willReturn(response);

            mockMvc.perform(
                    get(
                        "/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId
                       )
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.tableNumber")
                        .value("T1"));
        }

        @Test
        void getById_shouldReturn404_whenTableDoesNotExist()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 999L;

            given(
                    restaurantTableService.getById(
                        restaurantId,
                        tableId
                        )
                 ).willThrow(
                     new ResourceNotFoundException(
                         "Restaurant table not found"
                         )
                     );

            mockMvc.perform(
                    get(
                        "/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId
                       )
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        void getAll_shouldReturn200_withRestaurantTables()
            throws Exception {

            Long restaurantId = 1L;

            RestaurantTableResponse table1 =
                new RestaurantTableResponse(
                        1L,
                        restaurantId,
                        "T1",
                        UUID.randomUUID(),
                        TableStatus.AVAILABLE,
                        "https://example.com/1"
                        );

            RestaurantTableResponse table2 =
                new RestaurantTableResponse(
                        2L,
                        restaurantId,
                        "T2",
                        UUID.randomUUID(),
                        TableStatus.OCCUPIED,
                        "https://example.com/2"
                        );

            given(
                    restaurantTableService.getAll(
                        anyLong(),
                        any()
                        )
                 ).willReturn(
                     new PageImpl<>(
                         List.of(table1, table2),
                         PageRequest.of(0, 20),
                         2
                         )
                     );

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/tables", restaurantId)
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].tableNumber").value("T1"))
                .andExpect(jsonPath("$.data.content[1].tableNumber").value("T2"));
        }

        @Test
        void getAll_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

            Long restaurantId = 999L;

            given(
                    restaurantTableService.getAll(
                        anyLong(),
                        any()
                        )
                 ).willThrow(
                     new ResourceNotFoundException("Restaurant not found")
                     );

            mockMvc.perform(
                    get("/api/v1/restaurants/{restaurantId}/tables", restaurantId)
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void update_shouldReturn200_whenTableIsUpdated()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 10L;

            UpdateRestaurantTableRequest request =
                new UpdateRestaurantTableRequest(
                        "T2",
                        TableStatus.OCCUPIED
                        );

            RestaurantTableResponse response =
                new RestaurantTableResponse(
                        tableId,
                        restaurantId,
                        "T2",
                        UUID.randomUUID(),
                        TableStatus.OCCUPIED,
                        "https://example.com"
                        );

            given(
                    restaurantTableService.update(
                        restaurantId,
                        tableId,
                        request
                        )
                 ).willReturn(response);

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant table updated successfully."))
                .andExpect(jsonPath("$.data.tableNumber").value("T2"))
                .andExpect(jsonPath("$.data.status").value("OCCUPIED"));
        }

        @Test
        void update_shouldReturn404_whenTableDoesNotExist()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 999L;

            UpdateRestaurantTableRequest request =
                new UpdateRestaurantTableRequest(
                        "T2",
                        TableStatus.OCCUPIED
                        );

            given(
                    restaurantTableService.update(
                        restaurantId,
                        tableId,
                        request
                        )
                 ).willThrow(
                     new ResourceNotFoundException("Restaurant table not found")
                     );

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void update_shouldReturn409_whenTableAlreadyExists()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 10L;

            UpdateRestaurantTableRequest request =
                new UpdateRestaurantTableRequest(
                        "T2",
                        TableStatus.AVAILABLE
                        );

            given(
                    restaurantTableService.update(
                        restaurantId,
                        tableId,
                        request
                        )
                 ).willThrow(
                     new ResourceAlreadyExistsException(
                         "Table number already exists for this restaurant"
                         )
                     );

            mockMvc.perform(
                    put("/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void delete_shouldReturn200_whenTableIsDeleted()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 10L;

            doNothing()
                .when(restaurantTableService)
                .delete(restaurantId, tableId);

            mockMvc.perform(
                    delete("/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId)
                    .with(csrf())
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(restaurantTableService)
                .delete(restaurantId, tableId);
        }

        @Test
        void delete_shouldReturn404_whenTableDoesNotExist()
            throws Exception {

            Long restaurantId = 1L;
            Long tableId = 999L;

            doThrow(
                    new ResourceNotFoundException("Restaurant table not found")
                   )
                .when(restaurantTableService)
                .delete(restaurantId, tableId);

            mockMvc.perform(
                    delete("/api/v1/restaurants/{restaurantId}/tables/{tableId}",
                        restaurantId,
                        tableId)
                    .with(csrf())
                    )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void create_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

            Long restaurantId = 1L;

            CreateRestaurantTableRequest request =
                new CreateRestaurantTableRequest("");

            mockMvc.perform(
                    post("/api/v1/restaurants/{restaurantId}/tables",
                        restaurantId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request))
                    )
                .andExpect(status().isBadRequest());

            verify(restaurantTableService, never())
                .create(anyLong(), any(CreateRestaurantTableRequest.class));
        }
    }
