package com.cinect.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Maps ?city= query params (short slug, legacy English label, or provinces_new.code) to
 * {@code provinces_new.code} for filtering cinemas/showtimes — aligned with NestJS
 * {@code resolveCinemaProvinceCode}.
 */
public final class BookingCityResolver {

    private static final Map<String, String> SLUG_TO_PROVINCE = Map.ofEntries(
            Map.entry("hcm", "ho-chi-minh-city"),
            Map.entry("hn", "ha-noi"),
            Map.entry("dn", "da-nang"),
            Map.entry("hp", "hai-phong"),
            Map.entry("ct", "can-tho"),
            Map.entry("bd", "ho-chi-minh-city"),
            Map.entry("nt", "khanh-hoa"),
            Map.entry("vt", "ho-chi-minh-city"));

    private static final Map<String, String> LEGACY_CITY_TO_PROVINCE = Map.ofEntries(
            Map.entry("ho chi minh", "ho-chi-minh-city"),
            Map.entry("ha noi", "ha-noi"),
            Map.entry("da nang", "da-nang"),
            Map.entry("hai phong", "hai-phong"),
            Map.entry("can tho", "can-tho"),
            Map.entry("hue", "hue"),
            Map.entry("nha trang", "khanh-hoa"),
            Map.entry("binh duong", "ho-chi-minh-city"),
            Map.entry("vung tau", "ho-chi-minh-city"),
            Map.entry("thanh pho ho chi minh", "ho-chi-minh-city"));

    private static final Pattern PROVINCE_CODE = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    private BookingCityResolver() {
    }

    public static String resolveCinemaProvinceCode(String city) {
        if (city == null || city.isBlank()) {
            return null;
        }
        String raw = city.trim();
        String low = raw.toLowerCase(Locale.ROOT);
        if (SLUG_TO_PROVINCE.containsKey(low)) {
            return SLUG_TO_PROVINCE.get(low);
        }
        String folded = normalize(raw);
        if (LEGACY_CITY_TO_PROVINCE.containsKey(folded)) {
            return LEGACY_CITY_TO_PROVINCE.get(folded);
        }
        if (PROVINCE_CODE.matcher(low).matches()) {
            return low;
        }
        return null;
    }

    private static String normalize(String s) {
        String nfd = Normalizer.normalize(s.trim(), Normalizer.Form.NFD);
        String noMarks = nfd.replaceAll("\\p{M}+", "");
        return noMarks.toLowerCase(Locale.ROOT);
    }

    /** @deprecated use {@link #resolveCinemaProvinceCode(String)} */
    @Deprecated
    public static String resolveCinemaCityFilter(String city) {
        return resolveCinemaProvinceCode(city);
    }
}
