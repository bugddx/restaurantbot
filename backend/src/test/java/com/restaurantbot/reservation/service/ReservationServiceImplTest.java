package com.restaurantbot.reservation.service;

import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.dto.UpdateReservationRequest;
import com.restaurantbot.reservation.entity.Reservation;
import com.restaurantbot.reservation.entity.ReservationStatus;
import com.restaurantbot.reservation.mapper.ReservationMapper;
import com.restaurantbot.reservation.repository.ReservationRepository;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ReservationMapper reservationMapper;

    private ReservationServiceImpl service;

    private Restaurant restaurant;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new ReservationServiceImpl(
                reservationRepository,
                restaurantRepository,
                reservationMapper
        );

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .build();

        reservation = Reservation.builder()
                .id(100L)
                .restaurant(restaurant)
                .phoneNumber("919876543210")
                .guestCount(4)
                .reservationDate(LocalDate.now().plusDays(5))
                .reservationTime(LocalTime.of(19, 30))
                .status(ReservationStatus.PENDING)
                .build();
    }

    @Test
    void create_shouldCreateReservation() {
        CreateReservationRequest request =
                new CreateReservationRequest(
                        4,
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        "Window seat"
                );

        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        ReservationStatus.PENDING,
                        "Window seat"
                );

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(reservationMapper.toEntity(request))
                .thenReturn(reservation);

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        ReservationResponse result =
                service.create(1L, "919876543210", request);

        assertThat(result).isEqualTo(response);

        assertThat(reservation.getRestaurant())
                .isEqualTo(restaurant);

        assertThat(reservation.getPhoneNumber())
                .isEqualTo("919876543210");

        verify(restaurantRepository).findById(1L);
        verify(reservationMapper).toEntity(request);
        verify(reservationRepository).save(reservation);
        verify(reservationMapper).toResponse(reservation);
    }

    @Test
    void create_shouldThrowWhenRestaurantDoesNotExist() {
        CreateReservationRequest request =
                new CreateReservationRequest(
                        4,
                        LocalDate.now().plusDays(5),
                        LocalTime.of(19, 30),
                        null
                );

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.create(1L, "919876543210", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurant not found");

        verify(reservationRepository, never()).save(any());
        verify(reservationMapper, never()).toEntity(any());
    }

    @Test
    void getById_shouldReturnReservation() {
        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        ReservationStatus.PENDING,
                        null
                );

        when(reservationRepository.findByIdAndRestaurantId(100L, 1L))
                .thenReturn(Optional.of(reservation));

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        ReservationResponse result =
                service.getById(1L, 100L);

        assertThat(result).isEqualTo(response);

        verify(reservationRepository)
                .findByIdAndRestaurantId(100L, 1L);
    }

    @Test
    void getById_shouldThrowWhenReservationDoesNotExist() {
        when(reservationRepository.findByIdAndRestaurantId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getById(1L, 100L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reservation not found");
    }

    @Test
    void getAll_shouldReturnReservations() {
        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        ReservationStatus.PENDING,
                        null
                );

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(reservationRepository
                .findByRestaurantIdOrderByReservationDateAscReservationTimeAsc(1L))
                .thenReturn(List.of(reservation));

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        List<ReservationResponse> result =
                service.getAll(1L);

        assertThat(result)
                .containsExactly(response);

        verify(restaurantRepository).findById(1L);
        verify(reservationRepository)
                .findByRestaurantIdOrderByReservationDateAscReservationTimeAsc(1L);
    }

    @Test
    void getByPhoneNumber_shouldReturnReservations() {
        ReservationResponse response =
                new ReservationResponse(
                        100L,
                        1L,
                        "919876543210",
                        4,
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        ReservationStatus.PENDING,
                        null
                );

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(reservationRepository
                .findByRestaurantIdAndPhoneNumberOrderByReservationDateAscReservationTimeAsc(
                        1L,
                        "919876543210"))
                .thenReturn(List.of(reservation));

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        List<ReservationResponse> result =
                service.getByPhoneNumber(
                        1L,
                        "919876543210"
                );

        assertThat(result)
                .containsExactly(response);
    }

    @Test
    void update_shouldUpdateReservation() {
        UpdateReservationRequest request =
                new UpdateReservationRequest(
                        6,
                        reservation.getReservationDate().plusDays(1),
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
                        request.reservationDate(),
                        request.reservationTime(),
                        ReservationStatus.CONFIRMED,
                        "Updated notes"
                );

        when(reservationRepository.findByIdAndRestaurantId(100L, 1L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        ReservationResponse result =
                service.update(1L, 100L, request);

        assertThat(result).isEqualTo(response);

        verify(reservationMapper).update(request, reservation);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void update_shouldThrowWhenReservationDoesNotExist() {
        UpdateReservationRequest request =
                new UpdateReservationRequest(
                        6,
                        LocalDate.now().plusDays(10),
                        LocalTime.of(20, 0),
                        ReservationStatus.CONFIRMED,
                        null
                );

        when(reservationRepository.findByIdAndRestaurantId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(1L, 100L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reservation not found");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteReservation() {
        when(reservationRepository.findByIdAndRestaurantId(100L, 1L))
                .thenReturn(Optional.of(reservation));

        service.delete(1L, 100L);

        verify(reservationRepository)
                .delete(reservation);
    }

    @Test
    void delete_shouldThrowWhenReservationDoesNotExist() {
        when(reservationRepository.findByIdAndRestaurantId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.delete(1L, 100L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reservation not found");

        verify(reservationRepository, never()).delete(any());
    }
}
