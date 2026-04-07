-- Align room/showtime/pricing_rules.format with JPA RoomFormatConverter (stores plain strings like '2D', 'IMAX').
-- We migrate the underlying PostgreSQL enum columns to VARCHAR(16) while preserving existing values.

ALTER TABLE showtimes
    ALTER COLUMN format TYPE VARCHAR(16) USING format::text;

ALTER TABLE rooms
    ALTER COLUMN format TYPE VARCHAR(16) USING format::text;

ALTER TABLE pricing_rules
    ALTER COLUMN format TYPE VARCHAR(16) USING format::text;

