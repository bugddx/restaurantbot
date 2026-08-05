CREATE TABLE menu_categories (
    id BIGSERIAL PRIMARY KEY,

    restaurant_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,

    display_order INTEGER NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_category_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_restaurant_category_name
        UNIQUE (restaurant_id, name)
);

CREATE INDEX idx_menu_category_restaurant
ON menu_categories(restaurant_id);

CREATE INDEX idx_menu_category_display_order
ON menu_categories(restaurant_id, display_order);
