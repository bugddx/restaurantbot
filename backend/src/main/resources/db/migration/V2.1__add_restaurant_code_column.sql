ALTER TABLE restaurants
ADD COLUMN restaurant_code VARCHAR(255);

ALTER TABLE restaurants
ADD CONSTRAINT uk_restaurants_code UNIQUE (restaurant_code);

