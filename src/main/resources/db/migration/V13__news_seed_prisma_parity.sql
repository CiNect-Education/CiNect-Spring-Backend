-- Extra news rows aligned with cinect-nest-backend prisma/seed.ts (not present in V2 demo seed).
-- Safe to run on DBs that already applied V2 (ON CONFLICT DO NOTHING).

INSERT INTO news_articles (title, slug, excerpt, content, category, image_url, author, tags)
VALUES
  (
    'Deadpool & Wolverine — kỷ lục phòng vé R-rated',
    'deadpool-wolverine-box-office-vn',
    'Phim MCU đầu tiên nhãn R ghi nhận suất chiếu đông khán giả tại các cụm rạp lớn.',
    'Deadpool & Wolverine (Shawn Levy) đánh dấu sự trở lại của Wade Wilson bên cạnh Wolverine, với doanh thu toàn cầu vượt một tỷ USD. Tại Việt Nam, các suất tối và cuối tuần tại hệ thống rạp quốc tế thường kín chỗ ở định dạng IMAX và 2D phụ đề.',
    'GENERAL',
    'https://placehold.co/800x400/1a1a2e/e94560?text=Deadpool+Wolverine',
    'CiNect Editorial',
    '["box office", "marvel", "deadpool"]'::jsonb
  ),
  (
    'Review: Inside Out 2 — Pixar và tuổi mới lớn',
    'review-inside-out-2',
    'Riley bước vào tuổi dậy thì; loạt cảm xúc mới lên màn ảnh.',
    'Inside Out 2 mở rộng thế giới nội tâm với Anxiety và các cảm xúc mới, phù hợp khán gia đại chúng. Phần hoạt hình và nhịp hài đặc trưng Pixar được giữ vững. Đánh giá của chúng tôi: phim gia đình đáng xem trên màn rộng.',
    'REVIEWS',
    'https://placehold.co/800x400/6c5ce7/ffeaa7?text=Inside+Out+2',
    'Movie Reviewer',
    '["review", "pixar", "animation"]'::jsonb
  ),
  (
    'Sắp chiếu: Avatar: Fire and Ash — hành tinh Pandora trở lại',
    'avatar-fire-and-ash-preview',
    'Phần tiếp theo của loạt phim Avatar, kỳ vọng định dạng 3D/IMAX.',
    'James Cameron tiếp tục mở rộng vũ trụ Pandora. Khán giả có thể theo dõi lịch chiếu và đặt vé sớm trên CiNect khi phim mở bán chính thức.',
    'TRAILERS',
    'https://placehold.co/800x400/d63031/dfe6e9?text=Avatar+Preview',
    'CiNect Editorial',
    '["avatar", "preview", "sci-fi"]'::jsonb
  )
ON CONFLICT (slug) DO NOTHING;
