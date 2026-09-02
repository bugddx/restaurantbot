package com.restaurantbot.reservation.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.dto.UpdateReservationRequest;
import com.restaurantbot.reservation.entity.Reservation;
import com.restaurantbot.reservation.repository.ReservationRepository;
import com.restaurantbot.reservation.mapper.ReservationMapper;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponse create(
            Long restaurantId,
            String phoneNumber,
            CreateReservationRequest request
    ) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        Reservation reservation = reservationMapper.toEntity(request);

        reservation.setRestaurant(restaurant);
        reservation.setPhoneNumber(phoneNumber);

        Reservation saved = reservationRepository.save(reservation);

        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getById(
            Long restaurantId,
            Long reservationId
    ) {
        Reservation reservation = reservationRepository
                .findByIdAndRestaurantId(reservationId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reservation not found"));

        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll(Long restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        return reservationRepository
                .findByRestaurantIdOrderByReservationDateAscReservationTimeAsc(
                        restaurantId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getByPhoneNumber(
            Long restaurantId,
            String phoneNumber
    ) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        return reservationRepository
                .findByRestaurantIdAndPhoneNumberOrderByReservationDateAscReservationTimeAsc(
                        restaurantId,
                        phoneNumber)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public ReservationResponse update(
            Long restaurantId,
            Long reservationId,
            UpdateReservationRequest request
    ) {
        Reservation reservation = reservationRepository
                .findByIdAndRestaurantId(reservationId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reservation not found"));

        reservationMapper.update(request, reservation);

        Reservation saved = reservationRepository.save(reservation);

        return reservationMapper.toResponse(saved);
    }

    @Override
    public void delete(
            Long restaurantId,
            Long reservationId
    ) {
        Reservation reservation = reservationRepository
                .findByIdAndRestaurantId(reservationId, restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reservation not found"));

        reservationRepository.delete(reservation);
    }
}
