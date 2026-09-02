ALTER TABLE conversations
ADD COLUMN reservation_guest_count INTEGER;

ALTER TABLE conversations
ADD COLUMN reservation_date DATE;

ALTER TABLE conversations
ADD COLUMN reservation_time TIME;
