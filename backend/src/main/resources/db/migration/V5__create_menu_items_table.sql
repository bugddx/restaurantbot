CREATE TABLE menu_items (

    id BIGSERIAL PRIMARY KEY,

    restaurant_id BIGINT NOT NULL,

    category_id BIGINT NOT NULL,

    name VARCHAR(150) NOT NULL,

    description VARCHAR(1000),

    price DECIMAL(10,2) NOT NULL,

    image_url VARCHAR(500),

    preparation_time INT,

    vegetarian BOOLEAN NOT NULL DEFAULT FALSE,

    vegan BOOLEAN NOT NULL DEFAULT FALSE,

    available BOOLEAN NOT NULL DEFAULT TRUE,

    display_order INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_item_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_menu_item_category
        FOREIGN KEY (category_id)
        REFERENCES menu_categories(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_category_menu_item
        UNIQUE (category_id, name)
);

CREATE INDEX idx_menu_item_restaurant
    ON menu_items (restaurant_id);

CREATE INDEX idx_menu_item_category
    ON menu_items (category_id);
