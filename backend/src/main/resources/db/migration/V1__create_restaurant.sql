CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE restaurants
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(200) NOT NULL,

    email VARCHAR(255) UNIQUE NOT NULL,

    phone VARCHAR(30),

    address VARCHAR(500),

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP
);
