package com.cinect.service;

import com.cinect.entity.Booking;
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
    private final MembershipRepository membershipRepository;

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
        var showtimes = showtimeRepository.findFiltered(null, null, from, to);
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
            result.add(Map.of(
                    "movieId", movie.getId(),
                    "title", movie.getTitle(),
                    "bookingCount", entry.getValue().size(),
                    "ticketCount", tickets,
                    "revenue", rev
            ));
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
            result.add(Map.of(
                    "cinemaId", cinema.getId(),
                    "name", cinema.getName(),
                    "bookingCount", entry.getValue().size(),
                    "ticketCount", tickets,
                    "revenue", rev
            ));
        }
        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        return result;
    }
}
