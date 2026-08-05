package com.restaurantbot.menuitem.repository;

import com.restaurantbot.menuitem.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Page<MenuItem> findByRestaurantId(
            Long restaurantId,
            Pageable pageable
    );

    Optional<MenuItem> findByIdAndRestaurantId(
            Long id,
            Long restaurantId
    );

    Page<MenuItem> findByCategoryId(
            Long categoryId,
            Pageable pageable
    );

    boolean existsByCategoryIdAndName(
            Long categoryId,
            String name
    );

    boolean existsByCategoryIdAndNameAndIdNot(
            Long categoryId,
            String name,
            Long itemId
    );

    Page<MenuItem> findByRestaurantIdAndCategoryId(
        Long restaurantId,
        Long categoryId,
        Pageable pageable
);
}
