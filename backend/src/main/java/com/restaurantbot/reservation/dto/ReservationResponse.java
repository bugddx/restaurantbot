package com.restaurantbot.reservation.dto;

import com.restaurantbot.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        Long id,
        Long restaurantId,
        String phoneNumber,
        Integer guestCount,
        LocalDate reservationDate,
        LocalTime reservationTime,
        ReservationStatus status,
        String notes
) {}
