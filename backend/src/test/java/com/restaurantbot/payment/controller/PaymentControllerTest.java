package com.restaurantbot.payment.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.restaurantbot.common.exception.GlobalExceptionHandler;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.payment.controller.PaymentController;
import com.restaurantbot.payment.dto.CreatePaymentRequest;
import com.restaurantbot.payment.dto.PaymentResponse;
import com.restaurantbot.payment.dto.UpdatePaymentRequest;
import com.restaurantbot.payment.entity.PaymentMethod;
import com.restaurantbot.payment.entity.PaymentStatus;
import com.restaurantbot.payment.service.PaymentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.json.JsonMapper jsonMapper; 

    @MockitoBean
    private PaymentService service;

    @Test
    void create_shouldReturn201_whenPaymentCreated()
            throws Exception {

        CreatePaymentRequest request =
                new CreatePaymentRequest(
                        PaymentMethod.CASH,
                        "TXN001"
                );

        PaymentResponse response =
                new PaymentResponse(
                        100L,
                        10L,
                        PaymentMethod.CASH,
                        PaymentStatus.COMPLETED,
                        new BigDecimal("500"),
                        "TXN001",
                        OffsetDateTime.now()
                );

        given(service.create(
                eq(1L),
                eq(10L),
                any(CreatePaymentRequest.class)))
                .willReturn(response);

        mockMvc.perform(
                        post("/api/v1/restaurants/1/orders/10/payments")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));

        verify(service)
                .create(
                        eq(1L),
                        eq(10L),
                        any(CreatePaymentRequest.class));
    }

    @Test
    void create_shouldReturn400_whenRequestInvalid()
            throws Exception {

        CreatePaymentRequest request =
                new CreatePaymentRequest(
                        null,
                        null
                );

        mockMvc.perform(
                        post("/api/v1/restaurants/1/orders/10/payments")
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

        PaymentResponse response =
                new PaymentResponse(
                        100L,
                        10L,
                        PaymentMethod.CASH,
                        PaymentStatus.COMPLETED,
                        new BigDecimal("500"),
                        "TXN001",
                        OffsetDateTime.now()
                );

        given(service.getById(
                1L,
                10L,
                100L))
                .willReturn(response);

        mockMvc.perform(
                        get("/api/v1/restaurants/1/orders/10/payments/100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    void getById_shouldReturn404()
            throws Exception {

        given(service.getById(
                1L,
                10L,
                100L))
                .willThrow(
                        new ResourceNotFoundException("Payment not found")
                );

        mockMvc.perform(
                        get("/api/v1/restaurants/1/orders/10/payments/100")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturn200()
            throws Exception {

        PaymentResponse response =
                new PaymentResponse(
                        100L,
                        10L,
                        PaymentMethod.CASH,
                        PaymentStatus.COMPLETED,
                        new BigDecimal("500"),
                        "TXN001",
                        OffsetDateTime.now()
                );

        given(service.getAll(1L))
                .willReturn(List.of(response));

        mockMvc.perform(
                        get("/api/v1/restaurants/1/orders/10/payments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
void update_shouldReturn200()
        throws Exception {

    UpdatePaymentRequest request =
            new UpdatePaymentRequest(
                    PaymentStatus.REFUNDED
            );

    PaymentResponse response =
            new PaymentResponse(
                    100L,
                    10L,
                    PaymentMethod.CASH,
                    PaymentStatus.REFUNDED,
                    new BigDecimal("500"),
                    "TXN001",
                    OffsetDateTime.now()
            );

    given(service.update(
            eq(1L),
            eq(10L),
            eq(100L),
            any(UpdatePaymentRequest.class)))
            .willReturn(response);

    mockMvc.perform(
                    put("/api/v1/restaurants/1/orders/10/payments/100")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("REFUNDED"));

    verify(service).update(
            eq(1L),
            eq(10L),
            eq(100L),
            any(UpdatePaymentRequest.class));
}

@Test
void update_shouldReturn404()
        throws Exception {

    UpdatePaymentRequest request =
            new UpdatePaymentRequest(
                    PaymentStatus.REFUNDED
            );

    given(service.update(
            eq(1L),
            eq(10L),
            eq(100L),
            any(UpdatePaymentRequest.class)))
            .willThrow(new ResourceNotFoundException("Payment not found"));

    mockMvc.perform(
                    put("/api/v1/restaurants/1/orders/10/payments/100")
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
                    100L
            );

    mockMvc.perform(
                    delete("/api/v1/restaurants/1/orders/10/payments/100")
                            .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

    verify(service)
            .delete(
                    1L,
                    10L,
                    100L
            );
}

@Test
void delete_shouldReturn404()
        throws Exception {

    doThrow(new ResourceNotFoundException("Payment not found"))
            .when(service)
            .delete(
                    1L,
                    10L,
                    100L
            );

    mockMvc.perform(
                    delete("/api/v1/restaurants/1/orders/10/payments/100")
                            .with(csrf())
            )
            .andExpect(status().isNotFound());
}
}
