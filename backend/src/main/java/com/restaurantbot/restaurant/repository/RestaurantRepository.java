package com.restaurantbot.restaurant.repository;

import com.restaurantbot.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository
    extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByEmail(String email);

    Optional<Restaurant> findByPublicId(UUID publicId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

}
