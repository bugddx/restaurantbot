package com.restaurantbot.menucategory.repository;

import com.restaurantbot.menucategory.entity.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuCategoryRepository
    extends JpaRepository<MenuCategory, Long> {

    /*    Page<MenuCategory> findByRestaurantId(
          Long restaurantId,
          Pageable pageable
          );
          */

    Page<MenuCategory> findByRestaurantIdOrderByDisplayOrderAsc(
            Long restaurantId,
            Pageable pageable
            );

    Optional<MenuCategory> findByIdAndRestaurantId(
            Long id,
            Long restaurantId
            );

    boolean existsByRestaurantIdAndName(
            Long restaurantId,
            String name
            );

    boolean existsByRestaurantIdAndNameAndIdNot(
            Long restaurantId,
            String name,
            Long categoryId
            );
}
