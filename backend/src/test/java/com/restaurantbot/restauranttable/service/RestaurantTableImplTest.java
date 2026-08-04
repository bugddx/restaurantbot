package com.restaurantbot.restauranttable.service;

import com.restaurantbot.common.exception.ResourceAlreadyExistsException;
import com.restaurantbot.common.exception.ResourceNotFoundException;
import com.restaurantbot.restaurant.entity.Restaurant;
import com.restaurantbot.restaurant.repository.RestaurantRepository;
import com.restaurantbot.restauranttable.dto.CreateRestaurantTableRequest;
import com.restaurantbot.restauranttable.dto.RestaurantTableResponse;
import com.restaurantbot.restauranttable.dto.UpdateRestaurantTableRequest;
import com.restaurantbot.restauranttable.entity.RestaurantTable;
import com.restaurantbot.restauranttable.entity.TableStatus;
import com.restaurantbot.restauranttable.mapper.RestaurantTableMapper;
import com.restaurantbot.restauranttable.repository.RestaurantTableRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantTableServiceImplTest {

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTableMapper restaurantTableMapper;

    @InjectMocks
    private RestaurantTableServiceImpl service;

    private Restaurant restaurant;
    private RestaurantTable table;

    private CreateRestaurantTableRequest createRequest;
    private UpdateRestaurantTableRequest updateRequest;

    private RestaurantTableResponse response;

    @BeforeEach
    void setUp() {

        restaurant = Restaurant.builder()
                .id(1L)
                .restaurantCode("REST001")
                .name("Demo Restaurant")
                .email("demo@test.com")
                .phone("9999999999")
                .address("Address")
                .publicId(UUID.randomUUID())
                .build();

        table = RestaurantTable.builder()
                .id(10L)
                .restaurant(restaurant)
                .tableNumber("T1")
                .qrToken(UUID.randomUUID())
                .status(TableStatus.AVAILABLE)
                .build();

        createRequest = new CreateRestaurantTableRequest(
                "T1"
        );

        updateRequest = new UpdateRestaurantTableRequest(
                "T2",
                TableStatus.OCCUPIED
        );

        response = new RestaurantTableResponse(
                10L,
                1L,
                "T1",
                table.getQrToken(),
                TableStatus.AVAILABLE,
                "https://example.com"
        );
    }

    @Test
    void create_shouldCreateTable_whenRequestIsValid() {

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(restaurantTableRepository.existsByRestaurantIdAndTableNumber(
                1L,
                "T1"))
                .thenReturn(false);

        when(restaurantTableMapper.toEntity(createRequest))
                .thenReturn(table);

        when(restaurantTableRepository.save(table))
                .thenReturn(table);

        when(restaurantTableMapper.toResponse(table))
                .thenReturn(response);

        RestaurantTableResponse result =
                service.create(1L, createRequest);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("T1", result.tableNumber());

        verify(restaurantRepository).findById(1L);
        verify(restaurantTableRepository)
                .existsByRestaurantIdAndTableNumber(1L, "T1");
        verify(restaurantTableRepository).save(table);
    }

    @Test
    void create_shouldThrowResourceNotFound_whenRestaurantDoesNotExist() {

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(1L, createRequest)
        );

        verify(restaurantTableRepository, never())
                .save(any());
    }

    @Test
    void create_shouldThrowResourceAlreadyExists_whenTableNumberAlreadyExists() {

        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(restaurant));

        when(restaurantTableRepository.existsByRestaurantIdAndTableNumber(
                1L,
                "T1"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(1L, createRequest)
        );

        verify(restaurantTableRepository, never())
                .save(any());
    }
        @Test
    void getById_shouldReturnTable_whenTableExists() {

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.of(table));

        when(restaurantTableMapper.toResponse(table))
                .thenReturn(response);

        RestaurantTableResponse result = service.getById(1L, 10L);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("T1", result.tableNumber());
        assertEquals(TableStatus.AVAILABLE, result.status());

        verify(restaurantTableRepository)
                .findByIdAndRestaurantId(10L, 1L);
        verify(restaurantTableMapper).toResponse(table);
    }

    @Test
    void getById_shouldThrowResourceNotFound_whenTableDoesNotExist() {

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getById(1L, 10L)
        );

        verify(restaurantTableMapper, never()).toResponse(any());
    }

    @Test
    void getAll_shouldReturnPageOfTables() {

        PageRequest pageable = PageRequest.of(0, 10);

        Page<RestaurantTable> tablePage =
                new PageImpl<>(List.of(table), pageable, 1);

        when(restaurantRepository.existsById(1L))
                .thenReturn(true);

        when(restaurantTableRepository.findByRestaurantId(1L, pageable))
                .thenReturn(tablePage);

        when(restaurantTableMapper.toResponse(table))
                .thenReturn(response);

        Page<RestaurantTableResponse> result =
                service.getAll(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("T1", result.getContent().getFirst().tableNumber());

        verify(restaurantRepository).existsById(1L);
        verify(restaurantTableRepository)
                .findByRestaurantId(1L, pageable);
    }

    @Test
    void getAll_shouldThrowResourceNotFound_whenRestaurantDoesNotExist() {

        PageRequest pageable = PageRequest.of(0, 10);

        when(restaurantRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAll(1L, pageable)
        );

        verify(restaurantTableRepository, never())
                .findByRestaurantId(anyLong(), any());
    }

    @Test
    void update_shouldUpdateTable_whenRequestIsValid() {

        RestaurantTable updatedTable = RestaurantTable.builder()
                .id(10L)
                .restaurant(restaurant)
                .tableNumber("T2")
                .qrToken(table.getQrToken())
                .status(TableStatus.OCCUPIED)
                .build();

        RestaurantTableResponse updatedResponse =
                new RestaurantTableResponse(
                        10L,
                        1L,
                        "T2",
                        table.getQrToken(),
                        TableStatus.OCCUPIED,
                        "https://example.com"
                );

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.of(table));

        when(restaurantTableRepository
                .existsByRestaurantIdAndTableNumberAndIdNot(
                        1L,
                        "T2",
                        10L))
                .thenReturn(false);

        doNothing().when(restaurantTableMapper)
                .updateEntity(updateRequest, table);

        when(restaurantTableRepository.save(table))
                .thenReturn(updatedTable);

        when(restaurantTableMapper.toResponse(updatedTable))
                .thenReturn(updatedResponse);

        RestaurantTableResponse result =
                service.update(1L, 10L, updateRequest);

        assertNotNull(result);
        assertEquals("T2", result.tableNumber());
        assertEquals(TableStatus.OCCUPIED, result.status());

        verify(restaurantTableMapper)
                .updateEntity(updateRequest, table);
        verify(restaurantTableRepository).save(table);
    }

    @Test
    void update_shouldThrowResourceNotFound_whenTableDoesNotExist() {

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(1L, 10L, updateRequest)
        );

        verify(restaurantTableRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowResourceAlreadyExists_whenTableNumberAlreadyExists() {

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.of(table));

        when(restaurantTableRepository
                .existsByRestaurantIdAndTableNumberAndIdNot(
                        1L,
                        "T2",
                        10L))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.update(1L, 10L, updateRequest)
        );

        verify(restaurantTableRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteTable_whenTableExists() {

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.of(table));

        doNothing().when(restaurantTableRepository)
                .delete(table);

        service.delete(1L, 10L);

        verify(restaurantTableRepository)
                .delete(table);
    }

    @Test
    void delete_shouldThrowResourceNotFound_whenTableDoesNotExist() {

        when(restaurantTableRepository.findByIdAndRestaurantId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(1L, 10L)
        );

        verify(restaurantTableRepository, never())
                .delete(any());
    }
}
