package com.restaurantbot.restaurant.service;

import com.restaurantbot.restaurant.dto.CreateRestaurantRequest;
import com.restaurantbot.restaurant.dto.RestaurantResponse;
import com.restaurantbot.restaurant.dto.UpdateRestaurantRequest;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.restaurant.mapper.RestaurantMapper;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository repository;

    @Mock
    private RestaurantMapper mapper;

    private RestaurantServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RestaurantServiceImpl(repository, mapper);
    }

    @Test
    void create_shouldCreateRestaurantSuccessfully() {

        CreateRestaurantRequest request =
                new CreateRestaurantRequest(
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        Restaurant restaurant = Restaurant.builder()
                .name("Pizza Palace")
                .email("owner@pizza.com")
                .phone("9876543210")
                .address("Delhi")
                .build();

        Restaurant savedRestaurant = Restaurant.builder()
                .name("Pizza Palace")
                .email("owner@pizza.com")
                .phone("9876543210")
                .address("Delhi")
                .build();

        RestaurantResponse response =
                new RestaurantResponse(
                        1L,
                        UUID.randomUUID(),
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        when(repository.existsByEmail(request.email()))
                .thenReturn(false);

        when(mapper.toEntity(request))
                .thenReturn(restaurant);

        when(repository.save(restaurant))
                .thenReturn(savedRestaurant);

        when(mapper.toResponse(savedRestaurant))
                .thenReturn(response);

        RestaurantResponse result =
                service.create(request);

        assertNotNull(result);
        assertEquals("Pizza Palace", result.name());
        assertEquals("owner@pizza.com", result.email());

        verify(repository)
                .existsByEmail(request.email());

        verify(repository)
                .save(restaurant);

        verify(mapper)
                .toEntity(request);

        verify(mapper)
                .toResponse(savedRestaurant);
    }

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {

        CreateRestaurantRequest request =
                new CreateRestaurantRequest(
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        when(repository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(request)
        );

        verify(repository)
                .existsByEmail(request.email());

        verify(repository, never())
                .save(any());

        verify(mapper, never())
                .toEntity(any());
    }

    @Test
    void findById_shouldReturnRestaurant_whenRestaurantExists() {

        Long id = 1L;

        Restaurant restaurant = Restaurant.builder()
                .name("Pizza Palace")
                .email("owner@pizza.com")
                .build();

        RestaurantResponse response =
                new RestaurantResponse(
                        id,
                        UUID.randomUUID(),
                        "Pizza Palace",
                        "owner@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        when(repository.findById(id))
                .thenReturn(Optional.of(restaurant));

        when(mapper.toResponse(restaurant))
                .thenReturn(response);

        RestaurantResponse result =
                service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Pizza Palace", result.name());

        verify(repository)
                .findById(id);

        verify(mapper)
                .toResponse(restaurant);
    }

    @Test
    void findById_shouldThrowException_whenRestaurantDoesNotExist() {

        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(id)
        );

        verify(repository)
                .findById(id);

        verify(mapper, never())
                .toResponse(any());
    }

    @Test
    void findAll_shouldReturnRestaurants() {

        Restaurant restaurant1 = Restaurant.builder()
                .name("Pizza Palace")
                .email("pizza@example.com")
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .name("Burger House")
                .email("burger@example.com")
                .build();

        RestaurantResponse response1 =
                new RestaurantResponse(
                        1L,
                        UUID.randomUUID(),
                        "Pizza Palace",
                        "pizza@example.com",
                        "9876543210",
                        "Delhi"
                );

        RestaurantResponse response2 =
                new RestaurantResponse(
                        2L,
                        UUID.randomUUID(),
                        "Burger House",
                        "burger@example.com",
                        "9876543211",
                        "Mumbai"
                );

        when(repository.findAll())
                .thenReturn(List.of(
                        restaurant1,
                        restaurant2
                ));

        when(mapper.toResponse(restaurant1))
                .thenReturn(response1);

        when(mapper.toResponse(restaurant2))
                .thenReturn(response2);

        List<RestaurantResponse> result =
                service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pizza Palace", result.get(0).name());
        assertEquals("Burger House", result.get(1).name());

        verify(repository)
                .findAll();

        verify(mapper)
                .toResponse(restaurant1);

        verify(mapper)
                .toResponse(restaurant2);
    }

    @Test
    void update_shouldUpdateRestaurantSuccessfully() {

        Long id = 1L;

        UpdateRestaurantRequest request =
                new UpdateRestaurantRequest(
                        "New Pizza Palace",
                        "newowner@pizza.com",
                        "9999999999",
                        "Mumbai"
                );

        Restaurant existingRestaurant =
                Restaurant.builder()
                        .name("Pizza Palace")
                        .email("owner@pizza.com")
                        .build();

        RestaurantResponse response =
                new RestaurantResponse(
                        id,
                        UUID.randomUUID(),
                        "New Pizza Palace",
                        "newowner@pizza.com",
                        "9999999999",
                        "Mumbai"
                );

        when(repository.findById(id))
                .thenReturn(Optional.of(existingRestaurant));

        when(repository.existsByEmailAndIdNot(
                request.email(),
                id
        )).thenReturn(false);

        doNothing()
                .when(mapper)
                .updateEntity(request, existingRestaurant);

        when(repository.save(existingRestaurant))
                .thenReturn(existingRestaurant);

        when(mapper.toResponse(existingRestaurant))
                .thenReturn(response);

        RestaurantResponse result =
                service.update(id, request);

        assertNotNull(result);
        assertEquals("New Pizza Palace", result.name());
        assertEquals("newowner@pizza.com", result.email());

        verify(repository)
                .findById(id);

        verify(repository)
                .existsByEmailAndIdNot(
                        request.email(),
                        id
                );

        verify(mapper)
                .updateEntity(
                        request,
                        existingRestaurant
                );

        verify(repository)
                .save(existingRestaurant);
    }

    @Test
    void update_shouldThrowException_whenEmailBelongsToAnotherRestaurant() {

        Long id = 1L;

        UpdateRestaurantRequest request =
                new UpdateRestaurantRequest(
                        "Pizza Palace",
                        "existing@pizza.com",
                        "9876543210",
                        "Delhi"
                );

        Restaurant restaurant =
                Restaurant.builder()
                        .name("Pizza Palace")
                        .email("owner@pizza.com")
                        .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(restaurant));

        when(repository.existsByEmailAndIdNot(
                request.email(),
                id
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.update(id, request)
        );

        verify(mapper, never())
                .updateEntity(
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any());
    }

    @Test
    void delete_shouldDeleteRestaurant_whenRestaurantExists() {

        Long id = 1L;

        Restaurant restaurant =
                Restaurant.builder()
                        .name("Pizza Palace")
                        .email("owner@pizza.com")
                        .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(restaurant));

        service.delete(id);

        verify(repository)
                .findById(id);

        verify(repository)
                .delete(restaurant);
    }

    @Test
    void delete_shouldThrowException_whenRestaurantDoesNotExist() {

        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(id)
        );

        verify(repository, never())
                .delete(any());
    }
}
