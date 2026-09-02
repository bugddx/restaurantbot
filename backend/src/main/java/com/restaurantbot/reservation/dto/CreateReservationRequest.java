package com.restaurantbot.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReservationRequest(

        @NotNull(message = "Guest count is required.")
        @Positive(message = "Guest count must be greater than zero.")
        Integer guestCount,

        @NotNull(message = "Reservation date is required.")
        @Future(message = "Reservation date must be in the future.")
        LocalDate reservationDate,

        @NotNull(message = "Reservation time is required.")
        LocalTime reservationTime,

        @Size(max = 500, message = "Notes must not exceed 500 characters.")
        String notes
) {}
