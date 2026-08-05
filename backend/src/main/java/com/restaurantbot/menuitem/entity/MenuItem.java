package com.restaurantbot.menuitem.entity;

import com.restaurantbot.common.entity.BaseEntity;
import com.restaurantbot.menucategory.entity.MenuCategory;
import com.restaurantbot.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "menu_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_menu_item",
                        columnNames = {"category_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_menu_item_restaurant", columnList = "restaurant_id"),
                @Index(name = "idx_menu_item_category", columnList = "category_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategory category;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String imageUrl;

    @Column(name = "preparation_time")
    private Integer preparationTime;

    @Column(nullable = false)
    private Boolean vegetarian;

    @Column(nullable = false)
    private Boolean vegan;

    @Column(nullable = false)
    private Boolean available;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @PrePersist
    void initializeDefaults() {

        if (vegetarian == null) {
            vegetarian = false;
        }

        if (vegan == null) {
            vegan = false;
        }

        if (available == null) {
            available = true;
        }

        if (displayOrder == null) {
            displayOrder = 0;
        }
    }
}
