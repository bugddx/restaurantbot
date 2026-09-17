package com.restaurantbot.reservation.controller;

import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.dto.UpdateReservationRequest;
import com.restaurantbot.reservation.entity.ReservationStatus;
import com.restaurantbot.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@WithMockUser
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void create_shouldReturnCreatedReservation() throws Exception {
        LocalDate date = LocalDate.now().plusDays(5);

        CreateReservationRequest request =
                new CreateReservationRequest(
                        4,
                        date,
                        LocalTime.of(19, 30),
                        "Window seat"
                );

        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        date,
                        LocalTime.of(19, 30),
                        ReservationStatus.PENDING,
                        "Window seat"
                );

        when(reservationService.create(
                eq(1L),
                eq("919876543210"),
                any(CreateReservationRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/restaurants/1/reservations")
                                .param("phoneNumber", "919876543210")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Reservation created successfully."))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.restaurantId").value(1))
                .andExpect(jsonPath("$.data.guestCount").value(4))
                .andExpect(jsonPath("$.data.status")
                        .value("PENDING"));

        verify(reservationService).create(
                eq(1L),
                eq("919876543210"),
                any(CreateReservationRequest.class)
        );
    }

    @Test
    void getById_shouldReturnReservation() throws Exception {
        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        LocalDate.now().plusDays(5),
                        LocalTime.of(19, 30),
                        ReservationStatus.PENDING,
                        null
                );

        when(reservationService.getById(1L, 100L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/restaurants/1/reservations/100")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.phoneNumber")
                        .value("919876543210"));
    }

    @Test
    void getAll_shouldReturnReservations() throws Exception {
        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        LocalDate.now().plusDays(5),
                        LocalTime.of(19, 30),
                        ReservationStatus.PENDING,
                        null
                );

        when(reservationService.getAll(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/v1/restaurants/1/reservations")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(100))
                .andExpect(jsonPath("$.data[0].guestCount").value(4));
    }

    @Test
    void getByPhoneNumber_shouldReturnReservations() throws Exception {
        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        LocalDate.now().plusDays(5),
                        LocalTime.of(19, 30),
                        ReservationStatus.PENDING,
                        null
                );

        when(reservationService.getByPhoneNumber(
                1L,
                "919876543210"
        )).thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/v1/restaurants/1/reservations/by-phone")
                                .param("phoneNumber", "919876543210")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].phoneNumber")
                        .value("919876543210"));
    }

    @Test
    void update_shouldReturnUpdatedReservation() throws Exception {
        LocalDate date = LocalDate.now().plusDays(6);

        UpdateReservationRequest request =
                new UpdateReservationRequest(
                        6,
                        date,
                        LocalTime.of(20, 0),
                        ReservationStatus.CONFIRMED,
                        "Updated notes"
                );

        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        6,
                        date,
                        LocalTime.of(20, 0),
                        ReservationStatus.CONFIRMED,
                        "Updated notes"
                );

        when(reservationService.update(
                eq(1L),
                eq(100L),
                any(UpdateReservationRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/restaurants/1/reservations/100")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.guestCount").value(6))
                .andExpect(jsonPath("$.data.status")
                        .value("CONFIRMED"));
    }

    @Test
    void delete_shouldDeleteReservation() throws Exception {
        doNothing().when(reservationService)
                .delete(1L, 100L);

        mockMvc.perform(
                        delete("/api/v1/restaurants/1/reservations/100")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Reservation deleted successfully."));

        verify(reservationService).delete(1L, 100L);
    }

    @Test
    void create_shouldReturnBadRequestWhenGuestCountIsMissing()
            throws Exception {

        String request = """
                {
                    "reservationDate": "%s",
                    "reservationTime": "19:30",
                    "notes": "Window seat"
                }
                """.formatted(LocalDate.now().plusDays(5));

        mockMvc.perform(
                        post("/api/v1/restaurants/1/reservations")
                                .param("phoneNumber", "919876543210")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnBadRequestWhenDateIsNotFuture()
            throws Exception {

        String request = """
                {
                    "guestCount": 4,
                    "reservationDate": "%s",
                    "reservationTime": "19:30"
                }
                """.formatted(LocalDate.now());

        mockMvc.perform(
                        post("/api/v1/restaurants/1/reservations")
                                .param("phoneNumber", "919876543210")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }
}
