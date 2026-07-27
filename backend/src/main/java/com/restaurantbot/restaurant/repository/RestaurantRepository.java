package com.restaurantbot.restaurant.repository;

import com.restaurantbot.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, UUID> {

    Optional<Restaurant> findByEmail(String email);

    boolean existsByEmail(String email);
}
