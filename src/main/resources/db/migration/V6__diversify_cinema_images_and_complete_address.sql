-- ============================================================
-- CiNect – V6: Diversify cinema images and complete addresses
-- ============================================================

-- 1) Complete address with ward/district/city if missing.
UPDATE cinemas
SET address = CONCAT_WS(', ', address, ward, district, city)
WHERE address IS NOT NULL
  AND TRIM(address) <> '';

-- 2) Assign diverse real photos deterministically by slug hash.
-- Use first hex of md5(slug) to distribute image URLs.
UPDATE cinemas
SET image_url = CASE SUBSTRING(md5(COALESCE(slug, '')) FROM 1 FOR 1)
  WHEN '0' THEN 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1600&q=80'
  WHEN '1' THEN 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1600&q=80'
  WHEN '2' THEN 'https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=1600&q=80'
  WHEN '3' THEN 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1600&q=80'
  WHEN '4' THEN 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=1600&q=80'
  WHEN '5' THEN 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=1600&q=80'
  WHEN '6' THEN 'https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=1600&q=80'
  WHEN '7' THEN 'https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=1600&q=80'
  WHEN '8' THEN 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1600&q=80'
  WHEN '9' THEN 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1600&q=80'
  WHEN 'a' THEN 'https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=1600&q=80'
  WHEN 'b' THEN 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1600&q=80'
  WHEN 'c' THEN 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=1600&q=80'
  WHEN 'd' THEN 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=1600&q=80'
  WHEN 'e' THEN 'https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=1600&q=80'
  ELSE      'https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=1600&q=80'
END;
