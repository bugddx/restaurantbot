package com.restaurantbot.restaurant.entity;

import com.restaurantbot.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID publicId;

    @Column(
            name = "restaurant_code",
            unique = true,
            length = 30
    )
    private String restaurantCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String address;

    @PrePersist
    void assignPublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }
}
