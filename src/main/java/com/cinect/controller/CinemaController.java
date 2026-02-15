package com.cinect.controller;

import com.cinect.dto.response.*;
import com.cinect.service.CinemaService;
import com.cinect.service.RoomService;
import com.cinect.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;
    private final RoomService roomService;
    private final ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CinemaResponse>>> findAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = cinemaService.findAll(city, search, page, limit);
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(data.getContent(), meta));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CinemaResponse>> findBySlug(@PathVariable String slug) {
        var data = cinemaService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRooms(@PathVariable UUID id) {
        var data = roomService.findByCinema(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getShowtimes(
            @PathVariable UUID id,
            @RequestParam(required = false) Instant startFrom,
            @RequestParam(required = false) Instant startTo) {
        var data = showtimeService.findFiltered(null, id, startFrom, startTo);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
