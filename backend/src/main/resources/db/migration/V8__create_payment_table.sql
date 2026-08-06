CREATE TABLE payments
(
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL UNIQUE,

    payment_method VARCHAR(20) NOT NULL,

    status VARCHAR(20) NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    transaction_reference VARCHAR(100),

    paid_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);
