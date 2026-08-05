package com.restaurantbot.menucategory.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menucategory.dto.CreateMenuCategoryRequest;
import com.restaurantbot.menucategory.dto.MenuCategoryResponse;
import com.restaurantbot.menucategory.dto.UpdateMenuCategoryRequest;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.menucategory.mapper.MenuCategoryMapper;
import com.restaurantbot.menucategory.repository.MenuCategoryRepository;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuCategoryServiceTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuCategoryMapper menuCategoryMapper;

    @InjectMocks
    private MenuCategoryServiceImpl service;

    private Restaurant restaurant;
    private MenuCategory category;
    private CreateMenuCategoryRequest createRequest;
    private UpdateMenuCategoryRequest updateRequest;
    private MenuCategoryResponse response;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .restaurantCode("REST001")
                .name("Pizza Palace")
                .email("owner@pizza.com")
                .phone("9876543210")
                .address("Delhi")
                .build();

        category = MenuCategory.builder()
                .id(1L)
                .restaurant(restaurant)
                .name("Pizza")
                .displayOrder(1)
                .active(true)
                .build();

        createRequest =
                new CreateMenuCategoryRequest(
                        "Pizza",
                        1
                );

        updateRequest =
                new UpdateMenuCategoryRequest(
                        "Premium Pizza",
                        2,
                        true
                );

        response =
                new MenuCategoryResponse(
                        1L,
                        1L,
                        "Pizza",
                        1,
                        true
                );
    }

        @Test
    void create_shouldCreateCategory() {

        given(restaurantRepository.findById(1L))
                .willReturn(Optional.of(restaurant));

        given(menuCategoryRepository.existsByRestaurantIdAndName(
                1L,
                createRequest.name()))
                .willReturn(false);

        given(menuCategoryMapper.toEntity(createRequest))
                .willReturn(category);

        given(menuCategoryRepository.save(category))
                .willReturn(category);

        given(menuCategoryMapper.toResponse(category))
                .willReturn(response);

        MenuCategoryResponse result =
                service.create(1L, createRequest);

        assertNotNull(result);
        assertEquals(response, result);

        verify(restaurantRepository).findById(1L);
        verify(menuCategoryRepository)
                .existsByRestaurantIdAndName(
                        1L,
                        createRequest.name());
        verify(menuCategoryMapper).toEntity(createRequest);
        verify(menuCategoryRepository).save(category);
        verify(menuCategoryMapper).toResponse(category);
    }

    @Test
    void create_shouldThrow_whenRestaurantNotFound() {

        given(restaurantRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(1L, createRequest)
        );

        verify(restaurantRepository).findById(1L);

        verify(menuCategoryRepository, never())
                .save(any());
    }

    @Test
    void create_shouldThrow_whenCategoryAlreadyExists() {

        given(restaurantRepository.findById(1L))
                .willReturn(Optional.of(restaurant));

        given(menuCategoryRepository.existsByRestaurantIdAndName(
                1L,
                createRequest.name()))
                .willReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(1L, createRequest)
        );

        verify(menuCategoryRepository, never())
                .save(any());
    }

    @Test
    void getById_shouldReturnCategory() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.of(category));

        given(menuCategoryMapper.toResponse(category))
                .willReturn(response);

        MenuCategoryResponse result =
                service.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(menuCategoryRepository)
                .findByIdAndRestaurantId(1L, 1L);
        verify(menuCategoryMapper)
                .toResponse(category);
    }

    @Test
    void getById_shouldThrow_whenCategoryNotFound() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getById(1L, 1L)
        );

        verify(menuCategoryRepository)
                .findByIdAndRestaurantId(1L, 1L);
    }

        @Test
    void getAll_shouldReturnPage() {

        Page<MenuCategory> page =
                new PageImpl<>(
                        List.of(category),
                        PageRequest.of(0, 10),
                        1
                );

        given(restaurantRepository.existsById(1L))
                .willReturn(true);

        given(menuCategoryRepository.findByRestaurantId(
                eq(1L),
                any(PageRequest.class)))
                .willReturn(page);

        given(menuCategoryMapper.toResponse(category))
                .willReturn(response);

        Page<MenuCategoryResponse> result =
                service.getAll(
                        1L,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().getFirst());

        verify(restaurantRepository).existsById(1L);
        verify(menuCategoryRepository)
                .findByRestaurantId(eq(1L), any(PageRequest.class));
    }

    @Test
    void getAll_shouldThrow_whenRestaurantNotFound() {

        given(restaurantRepository.existsById(1L))
                .willReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAll(
                        1L,
                        PageRequest.of(0, 10))
        );

        verify(menuCategoryRepository, never())
                .findByRestaurantId(anyLong(), any());
    }

    @Test
    void update_shouldUpdateCategory() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.of(category));

        given(menuCategoryRepository
                .existsByRestaurantIdAndNameAndIdNot(
                        1L,
                        updateRequest.name(),
                        1L))
                .willReturn(false);

        given(menuCategoryRepository.save(category))
                .willReturn(category);

        given(menuCategoryMapper.toResponse(category))
                .willReturn(response);

        MenuCategoryResponse result =
                service.update(
                        1L,
                        1L,
                        updateRequest);

        assertNotNull(result);

        verify(menuCategoryMapper)
                .updateEntity(updateRequest, category);

        verify(menuCategoryRepository)
                .save(category);
    }

    @Test
    void update_shouldThrow_whenCategoryNotFound() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(
                        1L,
                        1L,
                        updateRequest)
        );

        verify(menuCategoryRepository, never())
                .save(any());
    }

    @Test
    void update_shouldThrow_whenDuplicateNameExists() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.of(category));

        given(menuCategoryRepository
                .existsByRestaurantIdAndNameAndIdNot(
                        1L,
                        updateRequest.name(),
                        1L))
                .willReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.update(
                        1L,
                        1L,
                        updateRequest)
        );

        verify(menuCategoryRepository, never())
                .save(any());
    }

        @Test
    void delete_shouldDeleteCategory() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.of(category));

        doNothing().when(menuCategoryRepository)
                .delete(category);

        service.delete(1L, 1L);

        verify(menuCategoryRepository)
                .findByIdAndRestaurantId(1L, 1L);

        verify(menuCategoryRepository)
                .delete(category);
    }

    @Test
    void delete_shouldThrow_whenCategoryNotFound() {

        given(menuCategoryRepository.findByIdAndRestaurantId(
                1L,
                1L))
                .willReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(1L, 1L)
        );

        verify(menuCategoryRepository, never())
                .delete(any());
    }
}
