CREATE TABLE restaurant_tables
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    restaurant_id BIGINT NOT NULL,

    table_number VARCHAR(30) NOT NULL,

    qr_token UUID NOT NULL DEFAULT gen_random_uuid(),

    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_restaurant_tables_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_restaurant_table_number
        UNIQUE (restaurant_id, table_number),

    CONSTRAINT uk_restaurant_tables_qr_token
        UNIQUE (qr_token)
);

CREATE INDEX idx_restaurant_tables_restaurant_id
    ON restaurant_tables(restaurant_id);
