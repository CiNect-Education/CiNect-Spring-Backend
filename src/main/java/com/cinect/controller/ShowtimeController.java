package com.cinect.controller;

import com.cinect.dto.response.*;
import com.cinect.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> findFiltered(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Instant startFrom,
            @RequestParam(required = false) Instant startTo) {
        Instant from = startFrom;
        Instant to = startTo;
        if (date != null && !date.isBlank()) {
            var ld = LocalDate.parse(date.strip());
            ZoneId z = ZoneId.systemDefault();
            from = ld.atStartOfDay(z).toInstant();
            to = ld.plusDays(1).atStartOfDay(z).toInstant();
        } else {
            if (from == null) {
                from = Instant.now();
                to = to != null ? to : from.plus(Duration.ofDays(7));
            } else if (to == null) {
                to = from.plus(Duration.ofDays(7));
            }
        }
        var data = showtimeService.findFiltered(movieId, cinemaId, city, from, to);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> search(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String city) {
        var data = showtimeService.search(movieId, cinemaId, date, format, city);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> findById(@PathVariable UUID id) {
        var data = showtimeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<SeatMapResponse>> getSeats(@PathVariable UUID id) {
        var data = showtimeService.getSeatMap(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
