ALTER TABLE conversations
ADD COLUMN order_item_id BIGINT;

ALTER TABLE conversations
ADD CONSTRAINT fk_conversations_order_item
FOREIGN KEY (order_item_id)
REFERENCES order_items(id);
