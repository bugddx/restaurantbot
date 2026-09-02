package com.restaurantbot.reservation.service;

import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.dto.UpdateReservationRequest;

import java.util.List;

public interface ReservationService {

    ReservationResponse create(
            Long restaurantId,
            String phoneNumber,
            CreateReservationRequest request
    );

    ReservationResponse getById(
            Long restaurantId,
            Long reservationId
    );

    List<ReservationResponse> getAll(Long restaurantId);

    List<ReservationResponse> getByPhoneNumber(
            Long restaurantId,
            String phoneNumber
    );

    ReservationResponse update(
            Long restaurantId,
            Long reservationId,
            UpdateReservationRequest request
    );

    void delete(
            Long restaurantId,
            Long reservationId
    );
}
