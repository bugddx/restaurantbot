CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,

    phone_number VARCHAR(20) NOT NULL UNIQUE,

    state VARCHAR(50) NOT NULL,

    restaurant_id BIGINT NOT NULL,

    order_id BIGINT,

    menu_category_id BIGINT,

    menu_item_id BIGINT,

    last_interaction_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_conversation_restaurant
    FOREIGN KEY (restaurant_id)
    REFERENCES restaurants(id),

    CONSTRAINT fk_conversation_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id),

    CONSTRAINT fk_conversation_category
    FOREIGN KEY (menu_category_id)
    REFERENCES menu_categories(id),

    CONSTRAINT fk_conversation_item
    FOREIGN KEY (menu_item_id)
    REFERENCES menu_items(id)

);

-- Create index separately for faster phone number lookups
CREATE INDEX idx_conversation_phone
    ON conversations (phone_number);
