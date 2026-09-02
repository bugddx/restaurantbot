package com.restaurantbot.reservation.dto;

import com.restaurantbot.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateReservationRequest(

        @Positive(message = "Guest count must be greater than zero.")
        Integer guestCount,

        @Future(message = "Reservation date must be in the future.")
        LocalDate reservationDate,

        LocalTime reservationTime,

        ReservationStatus status,

        @Size(max = 500, message = "Notes must not exceed 500 characters.")
        String notes
) {}
