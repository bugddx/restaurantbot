CREATE TABLE orders
(
    id BIGSERIAL PRIMARY KEY,

    order_number VARCHAR(40) NOT NULL UNIQUE,

    restaurant_id BIGINT NOT NULL,

    table_id BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL,

    subtotal NUMERIC(10,2) NOT NULL DEFAULT 0,

    tax_amount NUMERIC(10,2) NOT NULL DEFAULT 0,

    discount_amount NUMERIC(10,2) NOT NULL DEFAULT 0,

    total_amount NUMERIC(10,2) NOT NULL DEFAULT 0,

    notes VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id),

    CONSTRAINT fk_order_table
        FOREIGN KEY (table_id)
        REFERENCES restaurant_tables(id)
);
