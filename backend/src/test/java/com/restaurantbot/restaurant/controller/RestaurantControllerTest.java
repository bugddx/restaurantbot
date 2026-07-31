package com.restaurantbot.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.restaurant.exception.RestaurantAlreadyExistsException;
import com.restaurantbot.restaurant.exception.RestaurantNotFoundException;
import com.restaurantbot.restaurant.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(RestaurantController.class)
@Import(GlobalExceptionHandler.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

@Autowired
private tools.jackson.databind.json.JsonMapper jsonMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void create_shouldReturn201_whenRestaurantIsCreated()
            throws Exception {

        CreateRestaurantRequest request =
                new CreateRestaurantRequest(
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        RestaurantResponse response =
                new RestaurantResponse(
                        1L,
                        UUID.randomUUID(),
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        given(restaurantService.create(any(CreateRestaurantRequest.class)))
                .willReturn(response);

        mockMvc.perform(
                        post("/api/v1/restaurants")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Pizza Palace"))
                .andExpect(jsonPath("$.data.email")
                        .value("owner@pizza.com"));

        verify(restaurantService).create(any(CreateRestaurantRequest.class));
    }

    @Test
    void create_shouldReturn409_whenEmailAlreadyExists()
            throws Exception {

        CreateRestaurantRequest request =
                new CreateRestaurantRequest(
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        given(restaurantService.create(any(CreateRestaurantRequest.class)))
                .willThrow(
                        new RestaurantAlreadyExistsException(
                                request.email()
                        )
                );

        mockMvc.perform(
                        post("/api/v1/restaurants")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Restaurant already exists with email: "
                                        + request.email()
                        )
                );
    }

    @Test
    void getById_shouldReturn200_whenRestaurantExists()
            throws Exception {

        Long id = 1L;

        RestaurantResponse response =
                new RestaurantResponse(
                        id,
                        UUID.randomUUID(),
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        given(restaurantService.findById(id))
                .willReturn(response);

        mockMvc.perform(
                        get("/api/v1/restaurants/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Pizza Palace"));
    }

    @Test
    void getById_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

        Long id = 999L;

        given(restaurantService.findById(id))
                .willThrow(
                        new RestaurantNotFoundException(id)
                );

        mockMvc.perform(
                        get("/api/v1/restaurants/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Restaurant not found with id: " + id
                        )
                );
    }

    @Test
    void getAll_shouldReturn200_withRestaurants()
            throws Exception {

        RestaurantResponse restaurant1 =
                new RestaurantResponse(
                        1L,
                        UUID.randomUUID(),
                        "Pizza Palace",
                        "pizza@example.com",
                        "9876543210",
                        "Delhi"
                );

        RestaurantResponse restaurant2 =
                new RestaurantResponse(
                        2L,
                        UUID.randomUUID(),
                        "Burger House",
                        "burger@example.com",
                        "9876543211",
                        "Mumbai"
                );

        given(restaurantService.findAll())
                .willReturn(List.of(
                        restaurant1,
                        restaurant2
                ));

        mockMvc.perform(
                        get("/api/v1/restaurants")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name")
                        .value("Pizza Palace"))
                .andExpect(jsonPath("$.data[1].name")
                        .value("Burger House"));
    }

    @Test
    void update_shouldReturn200_whenRestaurantIsUpdated()
            throws Exception {

        Long id = 1L;

        UpdateRestaurantRequest request =
                new UpdateRestaurantRequest(
                        "New Pizza Palace",
                        "newowner@pizza.com",
                        "9999999999",
                        "Mumbai"
                );

        RestaurantResponse response =
                new RestaurantResponse(
                        id,
                        UUID.randomUUID(),
                        "New Pizza Palace",
                        "newowner@pizza.com",
                        "9999999999",
                        "Mumbai"
                );

        given(
                restaurantService.update(
                        id,
                        request
                )
        ).willReturn(response);

        mockMvc.perform(
                        put("/api/v1/restaurants/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant updated successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("New Pizza Palace"))
                .andExpect(jsonPath("$.data.email")
                        .value("newowner@pizza.com"));
    }

    @Test
    void update_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

        Long id = 999L;

        UpdateRestaurantRequest request =
                new UpdateRestaurantRequest(
                        "New Pizza Palace",
                        "newowner@pizza.com",
                        "9999999999",
                        "Mumbai"
                );

        given(
                restaurantService.update(
                        id,
                        request
                )
        ).willThrow(
                new RestaurantNotFoundException(id)
        );

        mockMvc.perform(
                        put("/api/v1/restaurants/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void delete_shouldReturn204_whenRestaurantIsDeleted()
            throws Exception {

        Long id = 1L;

        doNothing()
                .when(restaurantService)
                .delete(id);

        mockMvc.perform(
                        delete("/api/v1/restaurants/{id}", id).with(csrf())
                )
                .andExpect(status().isNoContent());

        verify(restaurantService).delete(id);
    }

    @Test
    void delete_shouldReturn404_whenRestaurantDoesNotExist()
            throws Exception {

        Long id = 999L;

        doThrow(
                new RestaurantNotFoundException(id)
        )
                .when(restaurantService)
                .delete(id);

        mockMvc.perform(
                        delete("/api/v1/restaurants/{id}", id).with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        CreateRestaurantRequest request =
                new CreateRestaurantRequest(
                        "",
                        "invalid-email",
                        "9876543210",
                        "Delhi"
                );

        mockMvc.perform(
                        post("/api/v1/restaurants")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(restaurantService, org.mockito.Mockito.never())
                .create(any(CreateRestaurantRequest.class));
    }
}
