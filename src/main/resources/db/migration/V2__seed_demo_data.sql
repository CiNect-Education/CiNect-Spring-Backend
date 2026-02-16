-- ============================================================
-- CiNect – V2: Seed demo data (genres, movies, cinemas, rooms,
--              seats, showtimes, promotions, news, snacks, etc.)
-- ============================================================

-- ============================================================
-- USERS (admin + demo)
-- ============================================================

INSERT INTO users (email, password_hash, full_name, phone, is_active, email_verified, city)
VALUES
  ('admin@cinect.vn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin CiNect', '0901234567', TRUE, TRUE, 'Ho Chi Minh'),
  ('user@cinect.vn',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyen Van A',  '0912345678', TRUE, TRUE, 'Ho Chi Minh')
ON CONFLICT (email) DO NOTHING;

-- Assign roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@cinect.vn' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'user@cinect.vn' AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- ============================================================
-- GENRES
-- ============================================================

INSERT INTO genres (name, slug) VALUES
  ('Action',    'action'),
  ('Comedy',    'comedy'),
  ('Drama',     'drama'),
  ('Horror',    'horror'),
  ('Sci-Fi',    'sci-fi'),
  ('Romance',   'romance'),
  ('Animation', 'animation'),
  ('Thriller',  'thriller'),
  ('Fantasy',   'fantasy'),
  ('Adventure', 'adventure')
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- MOVIES (with real TMDB poster images)
-- ============================================================

INSERT INTO movies (title, original_title, slug, description, poster_url, banner_url, trailer_url, duration, release_date, director, cast_members, language, subtitles, rating, rating_count, age_rating, formats, status)
VALUES
  (
    'Avengers: Secret Wars',
    'Avengers: Secret Wars',
    'avengers-secret-wars',
    'The Avengers face their greatest threat yet as the multiverse collides in an epic battle that will determine the fate of all realities. Heroes from across dimensions must unite against an enemy that threatens to destroy everything.',
    'https://image.tmdb.org/t/p/w500/f0YBuh4hyiAheXhh4JnJWoKi9g5.jpg',
    'https://image.tmdb.org/t/p/w1280/rytc6Lf4447C0CDncwFa4gxe0vY.jpg',
    'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
    165, '2026-01-15', 'The Russo Brothers',
    '["Robert Downey Jr.", "Chris Evans", "Scarlett Johansson", "Tom Holland"]'::jsonb,
    'English', 'Vietnamese', 8.5, 1250, 'C13',
    '["2D", "3D", "IMAX"]'::jsonb, 'NOW_SHOWING'
  ),
  (
    'Lật Mặt 8: Hồi Kết',
    'Face Off 8: The Finale',
    'lat-mat-8-hoi-ket',
    'Phần cuối cùng của loạt phim Lật Mặt đình đám. Mọi bí mật sẽ được hé lộ trong cuộc chiến cuối cùng giữa thiện và ác, nơi ranh giới giữa đúng và sai trở nên mờ nhạt.',
    'https://image.tmdb.org/t/p/w500/kP84jB5ClTA6cU3JEZ0z8xiVsz8.jpg',
    'https://image.tmdb.org/t/p/w1280/3m0j3hCS8kMAaP9El6Vy5Lqnyft.jpg',
    NULL,
    135, '2026-02-01', 'Ly Hai',
    '["Ly Hai", "Truong Giang", "Oc Thanh Van", "Huy Khanh"]'::jsonb,
    'Vietnamese', NULL, 7.8, 3400, 'C16',
    '["2D"]'::jsonb, 'NOW_SHOWING'
  ),
  (
    'Inside Out 3',
    'Inside Out 3',
    'inside-out-3',
    'Riley is now in college and faces a whole new set of emotions. Watch as Anxiety, Nostalgia, and Ambition join the team inside headquarters, creating hilarious and heartwarming adventures.',
    'https://image.tmdb.org/t/p/w500/wAIFnJ5OeFU7tTnCWHiROsszS29.jpg',
    'https://image.tmdb.org/t/p/w1280/p5ozvmdgsmbWe0H8Xk7Rc8SCwAB.jpg',
    NULL,
    105, '2026-02-10', 'Kelsey Mann',
    '["Amy Poehler", "Maya Hawke", "Ayo Edebiri", "Lewis Black"]'::jsonb,
    'English', 'Vietnamese', 8.2, 2100, 'P',
    '["2D", "3D"]'::jsonb, 'NOW_SHOWING'
  ),
  (
    'Dune: Part Three',
    'Dune: Part Three',
    'dune-part-three',
    'The epic conclusion to the Dune saga. Paul Atreides faces the consequences of his choices as the fate of the universe hangs in the balance. An explosive finale filled with breathtaking visuals.',
    'https://image.tmdb.org/t/p/w500/8QdnKQyZDlN6rBSrfU1V5PctfUu.jpg',
    'https://image.tmdb.org/t/p/w1280/o869RihWTdTyBcEZBjz0izvEsVf.jpg',
    NULL,
    175, '2026-03-20', 'Denis Villeneuve',
    '["Timothée Chalamet", "Zendaya", "Florence Pugh", "Austin Butler"]'::jsonb,
    'English', 'Vietnamese', 9.0, 500, 'C13',
    '["2D", "IMAX"]'::jsonb, 'COMING_SOON'
  ),
  (
    'Mai 2',
    'Mai 2',
    'mai-2',
    'Phần tiếp theo của bộ phim Mai đình đám. Câu chuyện tình yêu đầy cảm xúc tiếp tục với những bất ngờ mới, khi Mai phải đối mặt với quá khứ và tìm lại chính mình.',
    'https://image.tmdb.org/t/p/w500/2nF8xD200rcDawuCg5ObxxqA2fC.jpg',
    'https://image.tmdb.org/t/p/w1280/zZ6nRdNQNxRnZ1LQ2ttPBZl9AXV.jpg',
    NULL,
    125, '2026-04-10', 'Tran Thanh',
    '["Phuong Anh Dao", "Tuan Tran", "NSUT Viet Huong", "Ngoc Giau"]'::jsonb,
    'Vietnamese', NULL, 0, 0, 'C16',
    '["2D"]'::jsonb, 'COMING_SOON'
  ),
  (
    'The Batman 2',
    'The Batman Part II',
    'the-batman-2',
    'Bruce Wayne continues his journey as Gotham''s protector, facing new villains that threaten to tear the city apart. A dark, gripping sequel that pushes the boundaries of superhero storytelling.',
    'https://image.tmdb.org/t/p/w500/6kRczrPsqRmAlq4ix2jZsVV4Khr.jpg',
    'https://image.tmdb.org/t/p/w1280/xQyGkQ8ICa4lgifGr3oZjkm3AJ2.jpg',
    NULL,
    155, '2026-02-05', 'Matt Reeves',
    '["Robert Pattinson", "Zoë Kravitz", "Colin Farrell", "Jeffrey Wright"]'::jsonb,
    'English', 'Vietnamese', 8.7, 1800, 'C16',
    '["2D", "IMAX", "4DX"]'::jsonb, 'NOW_SHOWING'
  )
ON CONFLICT (slug) DO UPDATE SET
  poster_url = EXCLUDED.poster_url,
  banner_url = EXCLUDED.banner_url;

-- ============================================================
-- MOVIE-GENRE associations
-- ============================================================

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m, genres g
WHERE (m.slug = 'avengers-secret-wars' AND g.slug IN ('action', 'sci-fi', 'adventure'))
   OR (m.slug = 'lat-mat-8-hoi-ket'   AND g.slug IN ('action', 'thriller', 'drama'))
   OR (m.slug = 'inside-out-3'        AND g.slug IN ('animation', 'comedy', 'drama'))
   OR (m.slug = 'dune-part-three'     AND g.slug IN ('sci-fi', 'adventure', 'drama'))
   OR (m.slug = 'mai-2'               AND g.slug IN ('romance', 'drama'))
   OR (m.slug = 'the-batman-2'        AND g.slug IN ('action', 'thriller'))
ON CONFLICT DO NOTHING;

-- ============================================================
-- CINEMAS
-- ============================================================

INSERT INTO cinemas (name, slug, address, city, district, phone, email, image_url, amenities, latitude, longitude)
VALUES
  ('CiNect Landmark 81', 'cinect-landmark-81',
   'Tầng 3, Landmark 81, 720A Điện Biên Phủ, Phường 22', 'Ho Chi Minh', 'Binh Thanh',
   '028 7108 8881', 'landmark81@cinect.vn',
   'https://placehold.co/800x400/0984e3/dfe6e9?text=CiNect+Landmark+81',
   '["IMAX", "4DX", "Dolby Atmos", "VIP Lounge", "Parking", "F&B Court"]'::jsonb,
   10.7950, 106.7220),
  ('CiNect Vincom Center', 'cinect-vincom-center',
   'Tầng 5, Vincom Center, 72 Lê Thánh Tôn, Phường Bến Nghé', 'Ho Chi Minh', 'District 1',
   '028 3827 8888', 'vincom@cinect.vn',
   'https://placehold.co/800x400/00b894/dfe6e9?text=CiNect+Vincom',
   '["3D", "Dolby Atmos", "Couple Seats", "Cafe", "Parking"]'::jsonb,
   10.7769, 106.7009),
  ('CiNect Royal City', 'cinect-royal-city',
   'Tầng 4, Royal City, 72A Nguyễn Trãi, Phường Thượng Đình', 'Ha Noi', 'Thanh Xuan',
   '024 6262 8888', 'royalcity@cinect.vn',
   'https://placehold.co/800x400/e17055/dfe6e9?text=CiNect+Royal+City',
   '["IMAX", "3D", "VIP Lounge", "Parking", "Kids Zone"]'::jsonb,
   21.0018, 105.8156)
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- ROOMS
-- ============================================================

INSERT INTO rooms (cinema_id, name, format, total_seats, rows, columns)
SELECT c.id, r.name, r.format::room_format, r.total_seats, r.rows, r.columns
FROM cinemas c,
     (VALUES
        ('cinect-landmark-81',  'Screen 1 - IMAX',     'IMAX', 160, 10, 16),
        ('cinect-landmark-81',  'Screen 2 - Standard',  '2D',    96,  8, 12),
        ('cinect-landmark-81',  'Screen 3 - 4DX',       '4DX',   60,  6, 10),
        ('cinect-vincom-center','Screen 1 - Dolby',     'DOLBY',112,  8, 14),
        ('cinect-vincom-center','Screen 2 - Standard',  '2D',    96,  8, 12),
        ('cinect-royal-city',   'Screen 1 - IMAX',     'IMAX', 160, 10, 16),
        ('cinect-royal-city',   'Screen 2 - Standard',  '2D',    96,  8, 12)
     ) AS r(cinema_slug, name, format, total_seats, rows, columns)
WHERE c.slug = r.cinema_slug
ON CONFLICT (cinema_id, name) DO NOTHING;

-- ============================================================
-- SEATS (generate for each room)
-- ============================================================

INSERT INTO seats (room_id, row_label, number, type, status, is_aisle, price)
SELECT
    rm.id,
    chr(65 + gs_row.n),        -- A, B, C, ...
    gs_col.n + 1,              -- 1, 2, 3, ...
    CASE
        WHEN gs_row.n >= rm.rows - 2 THEN 'VIP'::seat_type
        WHEN gs_row.n = 0 AND (gs_col.n = 0 OR gs_col.n = rm.columns - 1) THEN 'DISABLED'::seat_type
        ELSE 'STANDARD'::seat_type
    END,
    'AVAILABLE'::seat_status,
    FALSE,
    CASE
        WHEN gs_row.n >= rm.rows - 2 THEN 120000
        WHEN gs_row.n = 0 AND (gs_col.n = 0 OR gs_col.n = rm.columns - 1) THEN 70000
        ELSE 85000
    END
FROM rooms rm
CROSS JOIN generate_series(0, rm.rows - 1) AS gs_row(n)
CROSS JOIN generate_series(0, rm.columns - 1) AS gs_col(n)
WHERE NOT EXISTS (
    SELECT 1 FROM seats s WHERE s.room_id = rm.id
);

-- ============================================================
-- SHOWTIMES (for NOW_SHOWING movies, next 5 days)
-- ============================================================

INSERT INTO showtimes (movie_id, room_id, cinema_id, start_time, end_time, base_price, format, language, subtitles)
SELECT
    m.id,
    rm.id,
    rm.cinema_id,
    (CURRENT_DATE + day_offset.d) + time_slot.t,
    (CURRENT_DATE + day_offset.d) + time_slot.t + (m.duration || ' minutes')::interval,
    85000,
    rm.format,
    m.language,
    m.subtitles
FROM movies m
CROSS JOIN rooms rm
CROSS JOIN generate_series(1, 5) AS day_offset(d)
CROSS JOIN (VALUES
    ('10:00'::time), ('13:30'::time), ('16:00'::time), ('19:30'::time), ('22:00'::time)
) AS time_slot(t)
WHERE m.status = 'NOW_SHOWING'
  AND ((CURRENT_DATE + day_offset.d) + time_slot.t) > NOW()
  -- Distribute: each movie gets one time slot per room per day to avoid overlap
  AND (
    (extract(hour from time_slot.t)::int + rm.id::text::int % 5) % 5
    = (CASE m.slug
         WHEN 'avengers-secret-wars' THEN 0
         WHEN 'lat-mat-8-hoi-ket' THEN 1
         WHEN 'inside-out-3' THEN 2
         WHEN 'the-batman-2' THEN 3
         ELSE 4
       END)
  )
ON CONFLICT DO NOTHING;

-- ============================================================
-- PROMOTIONS
-- ============================================================

INSERT INTO promotions (title, description, code, discount_type, discount_value, min_purchase, max_discount, usage_limit, start_date, end_date, image_url, conditions, status, is_trending)
VALUES
  ('Student Discount - 20% Off', 'Show your student ID and get 20% off on all weekday screenings.', 'STUDENT20', 'PERCENTAGE', 20, 0, 50000, 1000, '2026-01-01', '2026-06-30', 'https://placehold.co/600x300/0984e3/ffffff?text=Student+20%25+Off', 'Valid student ID required. Weekdays only.', 'ACTIVE', TRUE),
  ('Combo Deal - Buy 2 Get 1 Free', 'Purchase 2 movie tickets and get a free combo snack pack.', 'COMBO2026', 'FIXED', 50000, 150000, 50000, 500, '2026-02-01', '2026-03-31', 'https://placehold.co/600x300/e17055/ffffff?text=Combo+Deal', 'Minimum 2 tickets per transaction.', 'ACTIVE', TRUE),
  ('Valentine Special - 30% Off Couple Seats', 'Celebrate love with 30% discount on all couple seats throughout February.', 'LOVE2026', 'PERCENTAGE', 30, 200000, 100000, 200, '2026-02-01', '2026-02-28', 'https://placehold.co/600x300/e84393/ffffff?text=Valentine+30%25+Off', 'Couple seats only. Limited availability.', 'ACTIVE', TRUE),
  ('Weekend Family Pack', 'Get 15% off when buying 4 or more tickets on weekends.', 'FAMILY15', 'PERCENTAGE', 15, 300000, 150000, 300, '2026-01-01', '2026-12-31', 'https://placehold.co/600x300/00b894/ffffff?text=Family+Pack', 'Minimum 4 tickets. Weekends only.', 'ACTIVE', FALSE)
ON CONFLICT (code) DO NOTHING;

-- ============================================================
-- NEWS ARTICLES
-- ============================================================

INSERT INTO news_articles (title, slug, excerpt, content, category, image_url, author, tags)
VALUES
  ('Avengers: Secret Wars Breaks Opening Weekend Records', 'avengers-secret-wars-breaks-records', 'The latest Marvel blockbuster shattered box office records.', 'Avengers: Secret Wars has officially become the biggest opening weekend in Vietnamese cinema history.', 'GENERAL', 'https://placehold.co/800x400/1a1a2e/e94560?text=Box+Office+Record', 'CiNect Editorial', '["box office", "marvel", "avengers"]'::jsonb),
  ('CiNect Launches Premium IMAX Experience at Landmark 81', 'cinect-imax-landmark-81', 'Experience movies like never before with our new IMAX screen.', 'We are thrilled to announce the opening of our premium IMAX theater at CiNect Landmark 81.', 'GENERAL', 'https://placehold.co/800x400/0984e3/ffffff?text=IMAX+Launch', 'CiNect PR Team', '["IMAX", "landmark 81", "premium"]'::jsonb),
  ('Review: Inside Out 3 - A Heartwarming College Adventure', 'review-inside-out-3', 'Pixar delivers another emotional masterpiece.', 'Inside Out 3 takes Riley to college, introducing new emotions like Anxiety, Nostalgia, and Ambition.', 'REVIEWS', 'https://placehold.co/800x400/6c5ce7/ffeaa7?text=Inside+Out+3+Review', 'Movie Reviewer', '["review", "pixar", "animation"]'::jsonb),
  ('Coming Soon: Dune Part Three - Everything You Need to Know', 'dune-part-three-preview', 'The epic conclusion arrives March 2026.', 'Denis Villeneuve returns to complete his ambitious adaptation of Frank Herbert''s sci-fi masterpiece.', 'TRAILERS', 'https://placehold.co/800x400/d63031/dfe6e9?text=Dune+Preview', 'CiNect Editorial', '["dune", "preview", "sci-fi"]'::jsonb),
  ('How to Get the Best Seats at CiNect', 'guide-best-seats-cinect', 'Tips and tricks for choosing the perfect seats.', 'Finding the perfect seat can make or break your movie experience. For IMAX: sit in the center, about 2/3 back.', 'GUIDES', 'https://placehold.co/800x400/00b894/ffffff?text=Seat+Guide', 'CiNect Team', '["guide", "tips", "seats"]'::jsonb)
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- SNACKS
-- ============================================================

INSERT INTO snacks (cinema_id, name, description, price, image_url)
SELECT c.id, s.name, s.description, s.price, s.image_url
FROM cinemas c,
     (VALUES
        ('Popcorn (L)',   'Large butter popcorn',           55000, 'https://placehold.co/200x200/f9ca24/2d3436?text=Popcorn+L'),
        ('Popcorn (M)',   'Medium butter popcorn',          40000, 'https://placehold.co/200x200/f9ca24/2d3436?text=Popcorn+M'),
        ('Coca-Cola (L)', 'Large Coca-Cola',                35000, 'https://placehold.co/200x200/e74c3c/ffffff?text=Coca+Cola'),
        ('Combo Couple',  '2 Popcorn L + 2 Coca-Cola L',  150000, 'https://placehold.co/200x200/e84393/ffffff?text=Combo+Couple'),
        ('Combo Family',  '2 Popcorn L + 4 Drinks',       220000, 'https://placehold.co/200x200/00b894/ffffff?text=Combo+Family'),
        ('Nachos',        'Nachos with cheese sauce',       60000, 'https://placehold.co/200x200/fdcb6e/2d3436?text=Nachos'),
        ('Hot Dog',       'Classic hot dog',                45000, 'https://placehold.co/200x200/e17055/ffffff?text=Hot+Dog'),
        ('Water Bottle',  'Mineral water 500ml',            15000, 'https://placehold.co/200x200/74b9ff/2d3436?text=Water')
     ) AS s(name, description, price, image_url)
WHERE NOT EXISTS (
    SELECT 1 FROM snacks sn WHERE sn.cinema_id = c.id AND sn.name = s.name
);

-- ============================================================
-- PRICING RULES
-- ============================================================

INSERT INTO pricing_rules (name, seat_type, format, day_type, price)
VALUES
  ('Standard 2D - Weekday', 'STANDARD', '2D',   'WEEKDAY', 85000),
  ('Standard 2D - Weekend', 'STANDARD', '2D',   'WEEKEND', 100000),
  ('VIP 2D - Weekday',      'VIP',      '2D',   'WEEKDAY', 120000),
  ('VIP 2D - Weekend',      'VIP',      '2D',   'WEEKEND', 150000),
  ('IMAX - Weekday',        'STANDARD', 'IMAX', 'WEEKDAY', 150000),
  ('IMAX - Weekend',        'STANDARD', 'IMAX', 'WEEKEND', 180000),
  ('4DX - Weekday',         'STANDARD', '4DX',  'WEEKDAY', 170000),
  ('4DX - Weekend',         'STANDARD', '4DX',  'WEEKEND', 200000)
ON CONFLICT DO NOTHING;

-- ============================================================
-- CAMPAIGNS
-- ============================================================

INSERT INTO campaigns (title, slug, description, content, image_url, start_date, end_date, is_active)
VALUES
  ('Lunar New Year 2026', 'lunar-new-year-2026', 'Celebrate Tet with special movie screenings!', 'This Lunar New Year, CiNect brings you a festival of cinema. Enjoy special Tet-themed screenings and exclusive combos.', 'https://placehold.co/1200x500/e74c3c/f1c40f?text=Tet+2026', '2026-01-25', '2026-02-15', TRUE),
  ('Summer Blockbuster Season', 'summer-blockbusters-2026', 'The biggest movies of the year are coming this summer!', 'Summer 2026 promises an incredible lineup of blockbusters. Pre-book and save up to 25%.', 'https://placehold.co/1200x500/0984e3/ffffff?text=Summer+2026', '2026-05-01', '2026-08-31', TRUE)
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- GIFT CARDS
-- ============================================================

INSERT INTO gift_cards (title, description, value, price, status, image_url)
SELECT gc.title, gc.description, gc.value, gc.price, gc.status::gift_card_status, gc.image_url
FROM (VALUES
  ('Movie Night Gift Card',       'Perfect gift for movie lovers.',                      100000, 90000,  'AVAILABLE', 'https://placehold.co/400x250/6c5ce7/ffffff?text=100K+Gift+Card'),
  ('Premium Experience Gift Card', 'Enjoy a premium movie experience with VIP seats.',    300000, 270000, 'AVAILABLE', 'https://placehold.co/400x250/0984e3/ffffff?text=300K+Gift+Card'),
  ('Ultimate Cinema Package',      'The ultimate gift - 2 VIP tickets, combo, and drinks.',500000, 450000, 'AVAILABLE', 'https://placehold.co/400x250/e17055/ffffff?text=500K+Gift+Card')
) AS gc(title, description, value, price, status, image_url)
WHERE NOT EXISTS (
    SELECT 1 FROM gift_cards g WHERE g.title = gc.title
);
