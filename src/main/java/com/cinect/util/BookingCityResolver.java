package com.cinect.util;

import java.util.Locale;
import java.util.Map;

/**
 * Maps booking region query params (e.g. hcm) to {@code cinema.city} values in the DB.
 */
public final class BookingCityResolver {

    private static final Map<String, String> REGION_TO_DB = Map.ofEntries(
            Map.entry("hcm", "Ho Chi Minh"),
            Map.entry("hn", "Ha Noi"),
            Map.entry("dn", "Da Nang"),
            Map.entry("hp", "Hai Phong"),
            Map.entry("ct", "Can Tho"),
            Map.entry("bd", "Binh Duong"),
            Map.entry("nt", "Nha Trang"),
            Map.entry("vt", "Vung Tau"));

    private BookingCityResolver() {
    }

    public static String resolveCinemaCityFilter(String city) {
        if (city == null || city.isBlank()) {
            return null;
        }
        String t = city.trim();
        String mapped = REGION_TO_DB.get(t.toLowerCase(Locale.ROOT));
        return mapped != null ? mapped : t;
    }
}
