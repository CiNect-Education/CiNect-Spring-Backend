-- CiNect V4: provinces (34 new + 63 legacy) + cinema linkage (Nest prisma migration parity)

CREATE TABLE IF NOT EXISTS provinces_new (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(64) NOT NULL UNIQUE,
    name_vi VARCHAR(255) NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS provinces_legacy (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(64) NOT NULL UNIQUE,
    name_vi VARCHAR(255) NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    province_new_id UUID NOT NULL REFERENCES provinces_new(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_provinces_legacy_new ON provinces_legacy(province_new_id);

ALTER TABLE cinemas ADD COLUMN IF NOT EXISTS ward VARCHAR(200);
ALTER TABLE cinemas ADD COLUMN IF NOT EXISTS province_new_id UUID REFERENCES provinces_new(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_cinemas_province_new ON cinemas(province_new_id) WHERE is_active = TRUE;

INSERT INTO provinces_new (code, name_vi, name_en, sort_order) VALUES
  ('ha-noi', 'Hà Nội', 'Hanoi', 1),
  ('ho-chi-minh-city', 'Thành phố Hồ Chí Minh', 'Ho Chi Minh City', 2),
  ('hai-phong', 'Hải Phòng', 'Haiphong', 3),
  ('da-nang', 'Đà Nẵng', 'Da Nang', 4),
  ('can-tho', 'Cần Thơ', 'Can Tho', 5),
  ('hue', 'Huế', 'Hue', 6),
  ('dien-bien', 'Điện Biên', 'Dien Bien', 7),
  ('lai-chau', 'Lai Châu', 'Lai Chau', 8),
  ('lao-cai', 'Lào Cai', 'Lao Cai', 9),
  ('cao-bang', 'Cao Bằng', 'Cao Bang', 10),
  ('lang-son', 'Lạng Sơn', 'Lang Son', 11),
  ('tuyen-quang', 'Tuyên Quang', 'Tuyen Quang', 12),
  ('thai-nguyen', 'Thái Nguyên', 'Thai Nguyen', 13),
  ('son-la', 'Sơn La', 'Son La', 14),
  ('phu-tho', 'Phú Thọ', 'Phu Tho', 15),
  ('bac-ninh', 'Bắc Ninh', 'Bac Ninh', 16),
  ('quang-ninh', 'Quảng Ninh', 'Quang Ninh', 17),
  ('hung-yen', 'Hưng Yên', 'Hung Yen', 18),
  ('ninh-binh', 'Ninh Bình', 'Ninh Binh', 19),
  ('thanh-hoa', 'Thanh Hóa', 'Thanh Hoa', 20),
  ('nghe-an', 'Nghệ An', 'Nghe An', 21),
  ('ha-tinh', 'Hà Tĩnh', 'Ha Tinh', 22),
  ('quang-tri', 'Quảng Trị', 'Quang Tri', 23),
  ('quang-ngai', 'Quảng Ngãi', 'Quang Ngai', 24),
  ('gia-lai', 'Gia Lai', 'Gia Lai', 25),
  ('dak-lak', 'Đắk Lắk', 'Dak Lak', 26),
  ('khanh-hoa', 'Khánh Hòa', 'Khanh Hoa', 27),
  ('lam-dong', 'Lâm Đồng', 'Lam Dong', 28),
  ('tay-ninh', 'Tây Ninh', 'Tay Ninh', 29),
  ('dong-nai', 'Đồng Nai', 'Dong Nai', 30),
  ('dong-thap', 'Đồng Tháp', 'Dong Thap', 31),
  ('an-giang', 'An Giang', 'An Giang', 32),
  ('vinh-long', 'Vĩnh Long', 'Vinh Long', 33),
  ('ca-mau', 'Cà Mau', 'Ca Mau', 34)
ON CONFLICT (code) DO NOTHING;

INSERT INTO provinces_legacy (code, name_vi, name_en, province_new_id)
SELECT v.code, v.name_vi, v.name_en, n.id
FROM (VALUES
  ('an-giang', 'An Giang', 'An Giang', 'an-giang'::text),
  ('bac-giang', 'Bắc Giang', 'Bac Giang', 'bac-ninh'::text),
  ('bac-kan', 'Bắc Kạn', 'Bac Kan', 'thai-nguyen'::text),
  ('bac-lieu', 'Bạc Liêu', 'Bac Lieu', 'ca-mau'::text),
  ('bac-ninh', 'Bắc Ninh', 'Bac Ninh', 'bac-ninh'::text),
  ('ben-tre', 'Bến Tre', 'Ben Tre', 'vinh-long'::text),
  ('binh-dinh', 'Bình Định', 'Binh Dinh', 'gia-lai'::text),
  ('binh-duong', 'Bình Dương', 'Binh Duong', 'ho-chi-minh-city'::text),
  ('binh-phuoc', 'Bình Phước', 'Binh Phuoc', 'dong-nai'::text),
  ('binh-thuan', 'Bình Thuận', 'Binh Thuan', 'lam-dong'::text),
  ('ca-mau', 'Cà Mau', 'Ca Mau', 'ca-mau'::text),
  ('cao-bang', 'Cao Bằng', 'Cao Bang', 'cao-bang'::text),
  ('can-tho', 'Cần Thơ', 'Can Tho', 'can-tho'::text),
  ('dak-lak', 'Đắk Lắk', 'Dak Lak', 'dak-lak'::text),
  ('dak-nong', 'Đắk Nông', 'Dak Nong', 'lam-dong'::text),
  ('dien-bien', 'Điện Biên', 'Dien Bien', 'dien-bien'::text),
  ('dong-nai', 'Đồng Nai', 'Dong Nai', 'dong-nai'::text),
  ('dong-thap', 'Đồng Tháp', 'Dong Thap', 'dong-thap'::text),
  ('gia-lai', 'Gia Lai', 'Gia Lai', 'gia-lai'::text),
  ('ha-giang', 'Hà Giang', 'Ha Giang', 'tuyen-quang'::text),
  ('hai-duong', 'Hải Dương', 'Hai Duong', 'hai-phong'::text),
  ('hai-phong', 'Hải Phòng', 'Haiphong', 'hai-phong'::text),
  ('ha-nam', 'Hà Nam', 'Ha Nam', 'ninh-binh'::text),
  ('ha-noi', 'Hà Nội', 'Hanoi', 'ha-noi'::text),
  ('ha-tinh', 'Hà Tĩnh', 'Ha Tinh', 'ha-tinh'::text),
  ('hoa-binh', 'Hòa Bình', 'Hoa Binh', 'phu-tho'::text),
  ('ho-chi-minh', 'Thành phố Hồ Chí Minh', 'Ho Chi Minh City', 'ho-chi-minh-city'::text),
  ('hung-yen', 'Hưng Yên', 'Hung Yen', 'hung-yen'::text),
  ('khanh-hoa', 'Khánh Hòa', 'Khanh Hoa', 'khanh-hoa'::text),
  ('kien-giang', 'Kiên Giang', 'Kien Giang', 'an-giang'::text),
  ('kon-tum', 'Kon Tum', 'Kon Tum', 'quang-ngai'::text),
  ('lai-chau', 'Lai Châu', 'Lai Chau', 'lai-chau'::text),
  ('lam-dong', 'Lâm Đồng', 'Lam Dong', 'lam-dong'::text),
  ('lang-son', 'Lạng Sơn', 'Lang Son', 'lang-son'::text),
  ('lao-cai', 'Lào Cai', 'Lao Cai', 'lao-cai'::text),
  ('long-an', 'Long An', 'Long An', 'tay-ninh'::text),
  ('nam-dinh', 'Nam Định', 'Nam Dinh', 'ninh-binh'::text),
  ('nghe-an', 'Nghệ An', 'Nghe An', 'nghe-an'::text),
  ('ninh-binh', 'Ninh Bình', 'Ninh Binh', 'ninh-binh'::text),
  ('ninh-thuan', 'Ninh Thuận', 'Ninh Thuan', 'khanh-hoa'::text),
  ('phu-tho', 'Phú Thọ', 'Phu Tho', 'phu-tho'::text),
  ('phu-yen', 'Phú Yên', 'Phu Yen', 'dak-lak'::text),
  ('quang-binh', 'Quảng Bình', 'Quang Binh', 'quang-tri'::text),
  ('quang-nam', 'Quảng Nam', 'Quang Nam', 'da-nang'::text),
  ('quang-ngai', 'Quảng Ngãi', 'Quang Ngai', 'quang-ngai'::text),
  ('quang-ninh', 'Quảng Ninh', 'Quang Ninh', 'quang-ninh'::text),
  ('quang-tri', 'Quảng Trị', 'Quang Tri', 'quang-tri'::text),
  ('soc-trang', 'Sóc Trăng', 'Soc Trang', 'can-tho'::text),
  ('son-la', 'Sơn La', 'Son La', 'son-la'::text),
  ('tay-ninh', 'Tây Ninh', 'Tay Ninh', 'tay-ninh'::text),
  ('thai-binh', 'Thái Bình', 'Thai Binh', 'hung-yen'::text),
  ('thai-nguyen', 'Thái Nguyên', 'Thai Nguyen', 'thai-nguyen'::text),
  ('thanh-hoa', 'Thanh Hóa', 'Thanh Hoa', 'thanh-hoa'::text),
  ('thua-thien-hue', 'Thừa Thiên Huế', 'Thua Thien Hue', 'hue'::text),
  ('tien-giang', 'Tiền Giang', 'Tien Giang', 'dong-thap'::text),
  ('tra-vinh', 'Trà Vinh', 'Tra Vinh', 'vinh-long'::text),
  ('tuyen-quang', 'Tuyên Quang', 'Tuyen Quang', 'tuyen-quang'::text),
  ('vinh-long', 'Vĩnh Long', 'Vinh Long', 'vinh-long'::text),
  ('vinh-phuc', 'Vĩnh Phúc', 'Vinh Phuc', 'phu-tho'::text),
  ('yen-bai', 'Yên Bái', 'Yen Bai', 'lao-cai'::text),
  ('da-nang', 'Đà Nẵng', 'Da Nang', 'da-nang'::text),
  ('ba-ria-vung-tau', 'Bà Rịa – Vũng Tàu', 'Ba Ria - Vung Tau', 'ho-chi-minh-city'::text),
  ('hau-giang', 'Hậu Giang', 'Hau Giang', 'can-tho'::text)
) AS v(code, name_vi, name_en, new_code)
JOIN provinces_new n ON n.code = v.new_code
ON CONFLICT (code) DO NOTHING;

-- Backfill demo cinemas from V2 seed (English city labels)
UPDATE cinemas c SET province_new_id = pn.id FROM provinces_new pn WHERE c.city = 'Ho Chi Minh' AND pn.code = 'ho-chi-minh-city';
UPDATE cinemas c SET province_new_id = pn.id FROM provinces_new pn WHERE c.city = 'Ha Noi' AND pn.code = 'ha-noi';
