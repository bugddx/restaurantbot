package com.restaurantbot.reservation.repository;

import com.restaurantbot.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRestaurantIdOrderByReservationDateAscReservationTimeAsc(
            Long restaurantId
    );

    List<Reservation> findByRestaurantIdAndPhoneNumberOrderByReservationDateAscReservationTimeAsc(
            Long restaurantId,
            String phoneNumber
    );

    Optional<Reservation> findByIdAndRestaurantId(
            Long id,
            Long restaurantId
    );

    List<Reservation> findByRestaurantIdAndReservationDate(
            Long restaurantId,
            LocalDate reservationDate
    );
}
