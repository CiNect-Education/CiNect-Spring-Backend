-- ============================================================
-- CiNect – V4: Generate massive showtimes (realistic pattern)
-- Notes:
-- - Creates 30 days of schedules for each room, 5-6 slots/day.
-- - Deterministic movie selection per room/day/slot.
-- ============================================================

-- Clear existing showtimes (user explicitly asked no DB-size concerns)
-- Must remove dependent transactional rows first.
DELETE FROM booking_items;
DELETE FROM bookings;
DELETE FROM hold_seats;
DELETE FROM holds;
DELETE FROM showtimes;

-- Ensure showtime IDs/timestamps are generated during seeding.
ALTER TABLE showtimes ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE showtimes ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE showtimes ALTER COLUMN updated_at SET DEFAULT now();

WITH
days AS (
  SELECT generate_series(0, 30) AS d
),
slots AS (
  -- slot_idx, start time (local)
  SELECT * FROM (VALUES
    (0, '09:30'::time),
    (1, '12:00'::time),
    (2, '14:30'::time),
    (3, '17:00'::time),
    (4, '19:30'::time),
    (5, '22:00'::time)
  ) AS s(slot_idx, t)
),
room_day_slot AS (
  SELECT
    rm.id AS room_id,
    rm.cinema_id AS cinema_id,
    rm.format AS room_format,
    d.d AS day_offset,
    s.slot_idx,
    (CURRENT_DATE + d.d) AS show_date,
    s.t AS show_time
  FROM rooms rm
  CROSS JOIN days d
  CROSS JOIN slots s
),
picked AS (
  SELECT
    rds.*,
    m.id AS movie_id,
    m.duration AS duration_min,
    m.language AS movie_language,
    m.subtitles AS movie_subtitles
  FROM room_day_slot rds
  CROSS JOIN LATERAL (
    SELECT m.*
    FROM movies m
    WHERE m.is_deleted = FALSE
      AND (
        m.status = 'NOW_SHOWING'
        OR (m.status = 'COMING_SOON' AND m.release_date <= (CURRENT_DATE + interval '30 days')::date)
      )
      -- Don't schedule coming-soon before release date
      AND (m.status <> 'COMING_SOON' OR m.release_date <= (rds.show_date)::date)
      -- Format compatibility: movie.formats contains room format (2D/3D/IMAX/4DX/DOLBY)
      AND (m.formats ? (rds.room_format::text))
    ORDER BY md5(rds.room_id::text || ':' || rds.day_offset::text || ':' || rds.slot_idx::text || ':' || m.id::text)
    LIMIT 1
  ) m
)
INSERT INTO showtimes (movie_id, room_id, cinema_id, start_time, end_time, base_price, format, language, subtitles)
SELECT
  p.movie_id,
  p.room_id,
  p.cinema_id,
  (p.show_date + p.show_time) AS start_time,
  (p.show_date + p.show_time) + (p.duration_min || ' minutes')::interval AS end_time,
  CASE
    WHEN p.room_format = 'IMAX' THEN 150000
    WHEN p.room_format = '4DX' THEN 170000
    WHEN p.room_format = 'DOLBY' THEN 120000
    WHEN p.room_format = '3D' THEN 105000
    ELSE 85000
  END AS base_price,
  p.room_format AS format,
  p.movie_language AS language,
  p.movie_subtitles AS subtitles
FROM picked p
WHERE (p.show_date + p.show_time) > NOW()
ON CONFLICT DO NOTHING;

