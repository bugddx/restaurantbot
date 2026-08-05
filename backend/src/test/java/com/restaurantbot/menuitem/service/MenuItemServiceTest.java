package com.restaurantbot.menuitem.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.menuitem.dto.CreateMenuItemRequest;
import com.restaurantbot.menuitem.dto.MenuItemResponse;
import com.restaurantbot.menuitem.dto.UpdateMenuItemRequest;
import com.restaurantbot.menuitem.entity.MenuItem;
import com.restaurantbot.menuitem.mapper.MenuItemMapper;
import com.restaurantbot.menuitem.repository.MenuItemRepository;
import com.restaurantbot.menucategory.entity.MenuCategory;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private MenuItemServiceImpl service;

    private Restaurant restaurant;
    private MenuCategory category;
    private MenuItem menuItem;
    private CreateMenuItemRequest createRequest;
    private UpdateMenuItemRequest updateRequest;
    private MenuItemResponse response;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
            .id(1L)
            .name("Pizza Palace")
            .email("pizza@test.com")
            .build();

        category = MenuCategory.builder()
            .id(10L)
            .restaurant(restaurant)
            .name("Pizza")
            .displayOrder(1)
            .build();

        menuItem = MenuItem.builder()
            .id(100L)
            .restaurant(restaurant)
            .category(category)
            .name("Margherita")
            .description("Classic pizza")
            .price(new BigDecimal("299.00"))
            .vegetarian(true)
            .vegan(false)
            .available(true)
            .displayOrder(1)
            .build();

        createRequest = new CreateMenuItemRequest(
                10L,
                "Margherita",
                "Classic pizza",
                new BigDecimal("299.00"),
                null,
                15,
                true,
                false,
                true,
                1
                );

        updateRequest = new UpdateMenuItemRequest(
                10L,
                "Farmhouse",
                "Loaded pizza",
                new BigDecimal("399.00"),
                null,
                20,
                true,
                false,
                true,
                2
                );

        response = new MenuItemResponse(
                100L,
                1L,
                10L,
                "Pizza",
                "Margherita",
                "Classic pizza",
                new BigDecimal("299.00"),
                null,
                15,
                true,
                false,
                true,
                1
                );
    }

    @Test
    void create_shouldCreateMenuItemSuccessfully() {

        given(restaurantRepository.findById(1L))
            .willReturn(Optional.of(restaurant));

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        given(menuItemRepository.existsByCategoryIdAndName(
                    10L,
                    "Margherita"))
            .willReturn(false);

        given(menuItemMapper.toEntity(createRequest))
            .willReturn(menuItem);

        given(menuItemRepository.save(menuItem))
            .willReturn(menuItem);

        given(menuItemMapper.toResponse(menuItem))
            .willReturn(response);

        MenuItemResponse result =
            service.create(1L, createRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Margherita");

        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void create_shouldThrowException_whenRestaurantNotFound() {

        given(restaurantRepository.findById(1L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.create(1L, createRequest))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenCategoryNotFound() {

        given(restaurantRepository.findById(1L))
            .willReturn(Optional.of(restaurant));

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.create(1L, createRequest))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenCategoryBelongsToAnotherRestaurant() {

        Restaurant anotherRestaurant = Restaurant.builder()
            .id(2L)
            .name("Burger House")
            .email("burger@test.com")
            .build();

        category.setRestaurant(anotherRestaurant);

        given(restaurantRepository.findById(1L))
            .willReturn(Optional.of(restaurant));

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        assertThatThrownBy(() ->
                service.create(1L, createRequest))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenMenuItemAlreadyExists() {

        given(restaurantRepository.findById(1L))
            .willReturn(Optional.of(restaurant));

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        given(menuItemRepository.existsByCategoryIdAndName(
                    10L,
                    "Margherita"))
            .willReturn(true);

        assertThatThrownBy(() ->
                service.create(1L, createRequest))
            .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnMenuItem() {

        given(menuItemRepository.findByIdAndRestaurantId(
                    100L,
                    1L))
            .willReturn(Optional.of(menuItem));

        given(menuItemMapper.toResponse(menuItem))
            .willReturn(response);

        MenuItemResponse result =
            service.getById(1L, 100L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
    }

    @Test
    void getById_shouldThrowException_whenMenuItemNotFound() {

        given(menuItemRepository.findByIdAndRestaurantId(
                    100L,
                    1L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getById(1L, 100L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_shouldReturnPageOfMenuItems() {

        Page<MenuItem> page =
            new PageImpl<>(
                    List.of(menuItem),
                    PageRequest.of(0, 10),
                    1
                    );

        given(restaurantRepository.existsById(1L))
            .willReturn(true);

        given(menuItemRepository.findByRestaurantId(
                    eq(1L),
                    any()))
            .willReturn(page);

        given(menuItemMapper.toResponse(menuItem))
            .willReturn(response);

        Page<MenuItemResponse> result =
            service.getAll(
                    1L,
                    PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAll_shouldThrowException_whenRestaurantNotFound() {

        given(restaurantRepository.existsById(1L))
            .willReturn(false);

        assertThatThrownBy(() ->
                service.getAll(
                    1L,
                    PageRequest.of(0, 10)))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByCategory_shouldReturnMenuItems() {

        Page<MenuItem> page =
            new PageImpl<>(
                    List.of(menuItem),
                    PageRequest.of(0, 10),
                    1
                    );

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        given(menuItemRepository.findByRestaurantIdAndCategoryId(
                    eq(1L),
                    eq(10L),
                    any()))
            .willReturn(page);

        given(menuItemMapper.toResponse(menuItem))
            .willReturn(response);

        Page<MenuItemResponse> result =
            service.getByCategory(
                    1L,
                    10L,
                    PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getByCategory_shouldThrowException_whenCategoryNotFound() {

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getByCategory(
                    1L,
                    10L,
                    PageRequest.of(0, 10)))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByCategory_shouldThrowException_whenCategoryBelongsToAnotherRestaurant() {

        Restaurant anotherRestaurant = Restaurant.builder()
            .id(2L)
            .build();

        category.setRestaurant(anotherRestaurant);

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        assertThatThrownBy(() ->
                service.getByCategory(
                    1L,
                    10L,
                    PageRequest.of(0, 10)))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateMenuItemSuccessfully() {

        given(menuItemRepository.findByIdAndRestaurantId(100L, 1L))
            .willReturn(Optional.of(menuItem));

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        given(menuItemRepository.existsByCategoryIdAndNameAndIdNot(
                    10L,
                    "Farmhouse",
                    100L))
            .willReturn(false);

        given(menuItemRepository.save(menuItem))
            .willReturn(menuItem);

        given(menuItemMapper.toResponse(menuItem))
            .willReturn(response);

        MenuItemResponse result =
            service.update(
                    1L,
                    100L,
                    updateRequest);

        assertThat(result).isNotNull();

        verify(menuItemMapper)
            .updateEntity(updateRequest, menuItem);

        verify(menuItemRepository)
            .save(menuItem);
    }

    @Test
    void update_shouldThrowException_whenDuplicateExists() {

        given(menuItemRepository.findByIdAndRestaurantId(100L, 1L))
            .willReturn(Optional.of(menuItem));

        given(menuCategoryRepository.findById(10L))
            .willReturn(Optional.of(category));

        given(menuItemRepository.existsByCategoryIdAndNameAndIdNot(
                    10L,
                    "Farmhouse",
                    100L))
            .willReturn(true);

        assertThatThrownBy(() ->
                service.update(
                    1L,
                    100L,
                    updateRequest))
            .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void update_shouldThrowException_whenMenuItemNotFound() {

        given(menuItemRepository.findByIdAndRestaurantId(
                    100L,
                    1L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(
                    1L,
                    100L,
                    updateRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteMenuItem() {

        given(menuItemRepository.findByIdAndRestaurantId(
                    100L,
                    1L))
            .willReturn(Optional.of(menuItem));

        service.delete(1L, 100L);

        verify(menuItemRepository).delete(menuItem);
    }

    @Test
    void delete_shouldThrowException_whenMenuItemNotFound() {

        given(menuItemRepository.findByIdAndRestaurantId(
                    100L,
                    1L))
            .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.delete(
                    1L,
                    100L))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
