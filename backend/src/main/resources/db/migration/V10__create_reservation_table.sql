CREATE TABLE reservations
(
    id BIGSERIAL PRIMARY KEY,

    restaurant_id BIGINT NOT NULL,

    phone_number VARCHAR(20) NOT NULL,

    guest_count INTEGER NOT NULL,

    reservation_date DATE NOT NULL,

    reservation_time TIME NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    notes VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservation_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_reservation_guest_count
        CHECK (guest_count > 0)
);

CREATE INDEX idx_reservation_restaurant
    ON reservations (restaurant_id);

CREATE INDEX idx_reservation_phone
    ON reservations (phone_number);

CREATE INDEX idx_reservation_date
    ON reservations (restaurant_id, reservation_date);
