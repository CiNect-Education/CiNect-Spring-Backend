-- ============================================================
-- CiNect – V5: Enrich cinema addresses and real photos
-- ============================================================

-- 1) Ensure address includes city/province label for readability.
UPDATE cinemas
SET address = CONCAT(address, ', ', city)
WHERE city IS NOT NULL
  AND TRIM(city) <> ''
  AND address IS NOT NULL
  AND TRIM(address) <> ''
  AND POSITION(LOWER(city) IN LOWER(address)) = 0;

-- 2) Replace placeholder images with real cinema-style photos.
UPDATE cinemas
SET image_url = CASE
  WHEN LOWER(slug) LIKE '%cgv%' THEN 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1600&q=80'
  WHEN LOWER(slug) LIKE '%lotte%' THEN 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1600&q=80'
  WHEN LOWER(slug) LIKE '%bhd%' THEN 'https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=1600&q=80'
  WHEN LOWER(slug) LIKE '%galaxy%' THEN 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1600&q=80'
  WHEN LOWER(slug) LIKE '%beta%' THEN 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=1600&q=80'
  ELSE 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=1600&q=80'
END
WHERE image_url IS NULL
   OR image_url LIKE 'https://placehold.co/%';
