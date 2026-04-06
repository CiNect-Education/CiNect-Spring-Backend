package com.cinect.service;

import com.cinect.entity.Booking;
import com.cinect.entity.Showtime;
import com.cinect.entity.enums.BookingStatus;
import com.cinect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final MembershipRepository membershipRepository;

    private static int parseRangeDays(String range) {
        if ("7d".equals(range)) return 7;
        if ("90d".equals(range)) return 90;
        return 30;
    }

    /**
     * Admin dashboard KPIs aligned with Nest {@code GET /admin/kpis}.
     */
    public Map<String, Object> getAdminKpis(String range) {
        int days = parseRangeDays(range);
        Instant to = Instant.now();
        Instant from = to.minusSeconds(days * 24L * 60 * 60);

        BigDecimal totalRevenue = bookingRepository.sumRevenueBetween(from, to);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        long totalBookings = bookingRepository.countCreatedBetween(from, to);
        long confirmedBookings = bookingRepository.countConfirmedBetween(from, to);
        long totalUsers = userRepository.count();
        long totalShowtimes = showtimeRepository.countActiveStartingFrom(from);
        long totalMovies = movieRepository.countByIsDeletedFalse();
        long totalCinemas = cinemaRepository.countByIsActiveTrue();

        long bookedSeats = bookingItemRepository.countBookedItemsForActiveShowtimesSince(from);
        long capacity = bookingItemRepository.sumRoomCapacityForShowtimesSince(from);
        double occupancyRate = capacity > 0 ? (double) bookedSeats / (double) capacity : 0.0;

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalRevenue", totalRevenue);
        map.put("totalBookings", totalBookings);
        map.put("totalUsers", totalUsers);
        map.put("confirmedBookings", confirmedBookings);
        map.put("totalShowtimes", totalShowtimes);
        map.put("totalMovies", totalMovies);
        map.put("totalCinemas", totalCinemas);
        map.put("occupancyRate", occupancyRate);
        return map;
    }

    /**
     * Daily revenue series aligned with Nest {@code GET /admin/revenue}.
     */
    public List<Map<String, Object>> getRevenueSeries(String range) {
        int days = parseRangeDays(range);
        Instant start = Instant.now().minusSeconds(days * 24L * 60 * 60);
        List<Booking> bookings = bookingRepository.findConfirmedOrCompletedSince(start);
        Map<String, BigDecimal> byDate = new HashMap<>();
        for (Booking b : bookings) {
            String key = LocalDate.ofInstant(b.getCreatedAt(), ZoneOffset.UTC).toString();
            BigDecimal amt = b.getFinalAmount() != null ? b.getFinalAmount() : BigDecimal.ZERO;
            byDate.merge(key, amt, BigDecimal::add);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC).minusDays(i);
            String k = d.toString();
            BigDecimal rev = byDate.getOrDefault(k, BigDecimal.ZERO);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", k);
            row.put("revenue", rev);
            out.add(row);
        }
        return out;
    }

    /**
     * Daily occupancy (0–1) aligned with Nest {@code GET /admin/occupancy}.
     */
    public List<Map<String, Object>> getOccupancySeries(String range) {
        int days = parseRangeDays(range);
        Instant start = Instant.now().minusSeconds(days * 24L * 60 * 60);
        List<Showtime> showtimes = showtimeRepository.findActiveStartingFrom(start);
        Map<UUID, Integer> bookedByShowtime = new HashMap<>();
        if (!showtimes.isEmpty()) {
            List<UUID> ids = showtimes.stream().map(Showtime::getId).toList();
            for (Object[] row : bookingItemRepository.countBookedItemsByShowtimeIds(ids)) {
                bookedByShowtime.put((UUID) row[0], ((Number) row[1]).intValue());
            }
        }
        Map<String, long[]> byDate = new HashMap<>();
        for (var st : showtimes) {
            String key = LocalDate.ofInstant(st.getStartTime(), ZoneOffset.UTC).toString();
            int cap = st.getRoom() != null && st.getRoom().getTotalSeats() != null
                    ? st.getRoom().getTotalSeats() : 0;
            int booked = bookedByShowtime.getOrDefault(st.getId(), 0);
            long[] agg = byDate.computeIfAbsent(key, k -> new long[]{0, 0});
            agg[0] += booked;
            agg[1] += cap;
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC).minusDays(i);
            String k = d.toString();
            long[] v = byDate.get(k);
            double occ = (v != null && v[1] > 0) ? (double) v[0] / (double) v[1] : 0.0;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", k);
            row.put("occupancy", occ);
            out.add(row);
        }
        return out;
    }

    /** Daily rows for admin sales report chart (date, revenue, bookings). */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSalesDailyReport(Instant from, Instant to) {
        Map<String, Object> revenue = getRevenue(from, to);
        return (List<Map<String, Object>>) revenue.get("daily");
    }

    public Map<String, Object> getRevenue(Instant from, Instant to) {
        var revenue = bookingRepository.sumRevenueBetween(from, to);
        var count = bookingRepository.countConfirmedBetween(from, to);

        // Daily revenue breakdown
        List<Map<String, Object>> daily = new ArrayList<>();
        LocalDate startDate = LocalDate.ofInstant(from, ZoneOffset.UTC);
        LocalDate endDate = LocalDate.ofInstant(to, ZoneOffset.UTC);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Instant dayStart = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            BigDecimal dayRevenue = bookingRepository.sumRevenueBetween(dayStart, dayEnd);
            long dayCount = bookingRepository.countConfirmedBetween(dayStart, dayEnd);
            daily.add(Map.of(
                    "date", date.toString(),
                    "revenue", dayRevenue != null ? dayRevenue : BigDecimal.ZERO,
                    "bookings", dayCount
            ));
        }

        return Map.of(
                "totalRevenue", revenue != null ? revenue : BigDecimal.ZERO,
                "totalBookings", count,
                "averageOrderValue", count > 0 && revenue != null
                        ? revenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO,
                "daily", daily
        );
    }

    public Map<String, Object> getOccupancy(Instant from, Instant to) {
        var showtimes = showtimeRepository.findFiltered(null, null, null, from, to);
        if (showtimes.isEmpty()) {
            return Map.of("averageOccupancy", 0.0, "showtimeCount", 0, "details", List.of());
        }

        List<Map<String, Object>> details = new ArrayList<>();
        double totalOccupancy = 0;

        for (var showtime : showtimes) {
            List<UUID> bookedSeats = bookingItemRepository.findBookedSeatIds(showtime.getId());
            int totalSeats = showtime.getRoom().getTotalSeats();
            double occupancy = totalSeats > 0 ? (double) bookedSeats.size() / totalSeats * 100 : 0;
            totalOccupancy += occupancy;
            details.add(Map.of(
                    "showtimeId", showtime.getId(),
                    "movieTitle", showtime.getMovie().getTitle(),
                    "cinemaName", showtime.getCinema().getName(),
                    "startTime", showtime.getStartTime().toString(),
                    "totalSeats", totalSeats,
                    "bookedSeats", bookedSeats.size(),
                    "occupancyPercent", Math.round(occupancy * 100.0) / 100.0
            ));
        }

        double avgOccupancy = totalOccupancy / showtimes.size();

        return Map.of(
                "averageOccupancy", Math.round(avgOccupancy * 100.0) / 100.0,
                "showtimeCount", showtimes.size(),
                "details", details
        );
    }

    public Map<String, Object> getPeakHours(Instant from, Instant to) {
        // Group confirmed bookings by hour of day
        var allBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> !b.getCreatedAt().isBefore(from) && b.getCreatedAt().isBefore(to))
                .collect(Collectors.toList());

        Map<Integer, Long> hourCounts = allBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> LocalTime.ofInstant(b.getCreatedAt(), ZoneOffset.UTC).getHour(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> peakHours = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            long count = hourCounts.getOrDefault(hour, 0L);
            peakHours.add(Map.of(
                    "hour", hour,
                    "label", String.format("%02d:00", hour),
                    "bookings", count
            ));
        }

        // Also group by day of week
        Map<DayOfWeek, Long> dayCounts = allBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> LocalDate.ofInstant(b.getCreatedAt(), ZoneOffset.UTC).getDayOfWeek(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> peakDays = new ArrayList<>();
        for (DayOfWeek dow : DayOfWeek.values()) {
            peakDays.add(Map.of(
                    "day", dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                    "bookings", dayCounts.getOrDefault(dow, 0L)
            ));
        }

        return Map.of(
                "byHour", peakHours,
                "byDayOfWeek", peakDays,
                "totalBookings", allBookings.size()
        );
    }

    public List<Map<String, Object>> getTopMovies(int limit) {
        // Get all confirmed bookings, group by movie, count and sum revenue
        var allBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.toList());

        Map<UUID, List<Booking>> byMovie = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getShowtime().getMovie().getId()));

        List<Map<String, Object>> topMovies = new ArrayList<>();
        for (var entry : byMovie.entrySet()) {
            var movie = entry.getValue().get(0).getShowtime().getMovie();
            BigDecimal revenue = entry.getValue().stream()
                    .map(Booking::getFinalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int tickets = entry.getValue().stream()
                    .mapToInt(b -> b.getItems().size())
                    .sum();

            topMovies.add(Map.of(
                    "movieId", movie.getId(),
                    "title", movie.getTitle(),
                    "posterUrl", movie.getPosterUrl() != null ? movie.getPosterUrl() : "",
                    "bookingCount", entry.getValue().size(),
                    "ticketCount", tickets,
                    "revenue", revenue
            ));
        }

        topMovies.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        return topMovies.stream().limit(limit).collect(Collectors.toList());
    }

    public Map<String, Object> getCustomerSegments() {
        long totalUsers = userRepository.count();
        long membersWithBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .map(b -> b.getUser().getId())
                .distinct()
                .count();

        // Segment by membership tier
        var allMemberships = membershipRepository.findAll();
        Map<String, Long> tierCounts = allMemberships.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getTier().getName(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> segments = new ArrayList<>();
        for (var entry : tierCounts.entrySet()) {
            segments.add(Map.of(
                    "tier", entry.getKey(),
                    "count", entry.getValue(),
                    "percentage", totalUsers > 0
                            ? Math.round((double) entry.getValue() / totalUsers * 10000.0) / 100.0
                            : 0.0
            ));
        }

        return Map.of(
                "totalUsers", totalUsers,
                "activeCustomers", membersWithBookings,
                "byTier", segments
        );
    }

    public Map<String, Object> getRevenueForecast(Instant from, Instant to) {
        var revenueData = getRevenue(from, to);
        @SuppressWarnings("unchecked")
        var daily = (List<Map<String, Object>>) revenueData.get("daily");
        if (daily == null || daily.size() < 2) {
            return Map.of("forecast", List.<Map<String, Object>>of(), "trend", "insufficient_data");
        }
        BigDecimal totalRevenue = (BigDecimal) revenueData.get("totalRevenue");
        long totalBookings = (Long) revenueData.get("totalBookings");
        double avgDaily = totalBookings > 0 ? totalRevenue.doubleValue() / daily.size() : 0;
        List<Map<String, Object>> forecast = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            forecast.add(Map.<String, Object>of(
                    "dayOffset", i,
                    "estimatedRevenue", BigDecimal.valueOf(avgDaily).setScale(2, RoundingMode.HALF_UP),
                    "estimatedBookings", totalBookings > 0 ? (long) (totalBookings * 1.0 / daily.size()) : 0L
            ));
        }
        return Map.of(
                "forecast", forecast,
                "averageDailyRevenue", totalRevenue.divide(BigDecimal.valueOf(daily.size()), 2, RoundingMode.HALF_UP),
                "periodDays", daily.size()
        );
    }

    public Map<String, Object> getSalesReport(Instant from, Instant to) {
        var revenue = getRevenue(from, to);
        return Map.of(
                "from", from.toString(),
                "to", to.toString(),
                "totalRevenue", revenue.get("totalRevenue"),
                "totalBookings", revenue.get("totalBookings"),
                "averageOrderValue", revenue.get("averageOrderValue"),
                "daily", revenue.get("daily")
        );
    }

    public List<Map<String, Object>> getMoviePerformanceReport(Instant from, Instant to) {
        var allBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> !b.getCreatedAt().isBefore(from) && b.getCreatedAt().isBefore(to))
                .collect(Collectors.toList());
        Map<UUID, List<Booking>> byMovie = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getShowtime().getMovie().getId()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : byMovie.entrySet()) {
            var movie = entry.getValue().get(0).getShowtime().getMovie();
            BigDecimal rev = entry.getValue().stream().map(Booking::getFinalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            int tickets = entry.getValue().stream().mapToInt(b -> b.getItems().size()).sum();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("movieId", movie.getId());
            row.put("movieTitle", movie.getTitle());
            row.put("title", movie.getTitle());
            row.put("bookings", entry.getValue().size());
            row.put("bookingCount", entry.getValue().size());
            row.put("ticketCount", tickets);
            row.put("revenue", rev);
            row.put("occupancy", 0.0);
            result.add(row);
        }
        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        return result;
    }

    public List<Map<String, Object>> getCinemaPerformanceReport(Instant from, Instant to) {
        var allBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> !b.getCreatedAt().isBefore(from) && b.getCreatedAt().isBefore(to))
                .collect(Collectors.toList());
        Map<UUID, List<Booking>> byCinema = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getShowtime().getCinema().getId()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : byCinema.entrySet()) {
            var cinema = entry.getValue().get(0).getShowtime().getCinema();
            BigDecimal rev = entry.getValue().stream().map(Booking::getFinalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            int tickets = entry.getValue().stream().mapToInt(b -> b.getItems().size()).sum();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("cinemaId", cinema.getId());
            row.put("cinemaName", cinema.getName());
            row.put("name", cinema.getName());
            row.put("bookings", entry.getValue().size());
            row.put("bookingCount", entry.getValue().size());
            row.put("ticketCount", tickets);
            row.put("revenue", rev);
            row.put("occupancy", 0.0);
            result.add(row);
        }
        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        return result;
    }

    /**
     * Forecast line chart: next 7 calendar days after {@code to}, estimated revenue = average daily in range.
     * Shape matches frontend {@code useAdminAnalyticsForecast}: {@code [{ date, revenue }]}.
     */
    public List<Map<String, Object>> getForecastSeries(Instant from, Instant to) {
        var revenueData = getRevenue(from, to);
        @SuppressWarnings("unchecked")
        var daily = (List<Map<String, Object>>) revenueData.get("daily");
        if (daily == null || daily.isEmpty()) {
            return List.of();
        }
        BigDecimal totalRevenue = (BigDecimal) revenueData.get("totalRevenue");
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        double avgDailyRev = daily.size() > 0 ? totalRevenue.doubleValue() / daily.size() : 0.0;

        LocalDate end = LocalDate.ofInstant(to, ZoneOffset.UTC);
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            LocalDate d = end.plusDays(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", d.toString());
            row.put("revenue", avgDailyRev);
            out.add(row);
        }
        return out;
    }

    /**
     * Occupancy heatmap: one row per (cinema, date) with ratio 0–1.
     * Matches frontend {@code useAdminAnalyticsOccupancy}.
     */
    public List<Map<String, Object>> getOccupancyByCinemaDate(Instant from, Instant to) {
        List<Showtime> showtimes = showtimeRepository.findActiveInRange(from, to);
        if (showtimes.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = showtimes.stream().map(Showtime::getId).toList();
        Map<UUID, Integer> bookedByShowtime = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Object[] row : bookingItemRepository.countBookedItemsByShowtimeIds(ids)) {
                bookedByShowtime.put((UUID) row[0], ((Number) row[1]).intValue());
            }
        }
        Map<UUID, Map<String, long[]>> byCinemaThenDate = new HashMap<>();
        Map<UUID, String> cinemaNames = new HashMap<>();
        for (Showtime st : showtimes) {
            UUID cid = st.getCinema().getId();
            cinemaNames.putIfAbsent(cid, st.getCinema().getName());
            String dateStr = LocalDate.ofInstant(st.getStartTime(), ZoneOffset.UTC).toString();
            int cap = st.getRoom().getTotalSeats() != null ? st.getRoom().getTotalSeats() : 0;
            int booked = bookedByShowtime.getOrDefault(st.getId(), 0);
            Map<String, long[]> byDate = byCinemaThenDate.computeIfAbsent(cid, k -> new HashMap<>());
            long[] agg = byDate.computeIfAbsent(dateStr, k -> new long[]{0, 0});
            agg[0] += booked;
            agg[1] += cap;
        }
        List<Map<String, Object>> out = new ArrayList<>();
        List<UUID> cinemaIds = new ArrayList<>(byCinemaThenDate.keySet());
        cinemaIds.sort(Comparator.comparing(cinemaNames::get));
        for (UUID cid : cinemaIds) {
            Map<String, long[]> byDate = byCinemaThenDate.get(cid);
            List<String> dates = new ArrayList<>(byDate.keySet());
            Collections.sort(dates);
            for (String dateStr : dates) {
                long[] v = byDate.get(dateStr);
                double occ = v[1] > 0 ? (double) v[0] / (double) v[1] : 0.0;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("cinemaId", cid.toString());
                row.put("cinemaName", cinemaNames.get(cid));
                row.put("date", dateStr);
                row.put("occupancy", occ);
                out.add(row);
            }
        }
        return out;
    }

    /**
     * Pie chart: {@code [{ segment, count, percentage }]} from tier breakdown.
     */
    public List<Map<String, Object>> getCustomerSegmentsChart() {
        Map<String, Object> raw = getCustomerSegments();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> byTier = (List<Map<String, Object>>) raw.get("byTier");
        if (byTier == null) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> t : byTier) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("segment", String.valueOf(t.get("tier")));
            row.put("count", ((Number) t.get("count")).longValue());
            row.put("percentage", ((Number) t.get("percentage")).doubleValue());
            out.add(row);
        }
        return out;
    }

    /**
     * 24 rows: hour + booking count (CONFIRMED) in range.
     */
    public List<Map<String, Object>> getPeakHoursSeries(Instant from, Instant to) {
        Map<String, Object> ph = getPeakHours(from, to);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> byHour = (List<Map<String, Object>>) ph.get("byHour");
        if (byHour == null) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> h : byHour) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("hour", ((Number) h.get("hour")).intValue());
            row.put("bookings", ((Number) h.get("bookings")).longValue());
            out.add(row);
        }
        return out;
    }
}
