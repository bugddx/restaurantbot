package com.restaurantbot.restauranttable.repository;

import com.restaurantbot.restauranttable.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long> {

    Page<RestaurantTable> findByRestaurantId(
            Long restaurantId,
            Pageable pageable
    );

    Optional<RestaurantTable> findByIdAndRestaurantId(
            Long id,
            Long restaurantId
    );

    Optional<RestaurantTable> findByQrToken(
            UUID qrToken
    );

    boolean existsByRestaurantIdAndTableNumber(
            Long restaurantId,
            String tableNumber
    );

    boolean existsByRestaurantIdAndTableNumberAndIdNot(
            Long restaurantId,
            String tableNumber,
            Long tableId
    );
}
