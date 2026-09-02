package com.restaurantbot.reservation.controller;

import com.restaurantbot.common.dto.ApiResponse;
import com.restaurantbot.common.util.ApiResponseUtil;
import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.dto.UpdateReservationRequest;
import com.restaurantbot.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @PathVariable Long restaurantId,
            @RequestParam String phoneNumber,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationResponse response =
                reservationService.create(restaurantId, phoneNumber, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Reservation created successfully.",
                        response
                )
        );
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getById(
            @PathVariable Long restaurantId,
            @PathVariable Long reservationId
    ) {
        ReservationResponse response =
                reservationService.getById(restaurantId, reservationId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Reservation retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAll(
            @PathVariable Long restaurantId
    ) {
        List<ReservationResponse> response =
                reservationService.getAll(restaurantId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Reservations retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/by-phone")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getByPhoneNumber(
            @PathVariable Long restaurantId,
            @RequestParam String phoneNumber
    ) {
        List<ReservationResponse> response =
                reservationService.getByPhoneNumber(
                        restaurantId,
                        phoneNumber
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Reservations retrieved successfully.",
                        response
                )
        );
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> update(
            @PathVariable Long restaurantId,
            @PathVariable Long reservationId,
            @Valid @RequestBody UpdateReservationRequest request
    ) {
        ReservationResponse response =
                reservationService.update(
                        restaurantId,
                        reservationId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Reservation updated successfully.",
                        response
                )
        );
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long reservationId
    ) {
        reservationService.delete(restaurantId, reservationId);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Reservation deleted successfully."
                )
        );
    }
}
