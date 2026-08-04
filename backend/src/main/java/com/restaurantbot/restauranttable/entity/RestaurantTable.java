package com.restaurantbot.restauranttable.entity;

import com.restaurantbot.common.entity.BaseEntity;
import com.restaurantbot.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "restaurant_tables",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_restaurant_table_number",
                        columnNames = {"restaurant_id", "table_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "table_number", nullable = false, length = 30)
    private String tableNumber;

    @Column(
            name = "qr_token",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID qrToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TableStatus status;

    @PrePersist
    void initializeDefaults() {
        if (qrToken == null) {
            qrToken = UUID.randomUUID();
        }

        if (status == null) {
            status = TableStatus.AVAILABLE;
        }
    }
}
