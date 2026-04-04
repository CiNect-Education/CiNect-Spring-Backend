package com.cinect.controller;

import com.cinect.dto.response.*;
import com.cinect.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Instant startFrom,
            @RequestParam(required = false) Instant startTo) {
        Instant normalizedStartFrom = startFrom;
        Instant normalizedStartTo = startTo;
        if (date != null && !date.isBlank() && startFrom == null && startTo == null) {
            var localDate = java.time.LocalDate.parse(date);
            normalizedStartFrom = localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
            normalizedStartTo = localDate.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        }

        var data = showtimeService.findFiltered(movieId, cinemaId, city, normalizedStartFrom, normalizedStartTo);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> search(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String format) {
        var data = showtimeService.search(movieId, cinemaId, city, date, format);
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
