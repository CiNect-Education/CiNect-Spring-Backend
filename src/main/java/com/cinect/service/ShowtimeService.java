package com.cinect.service;

import com.cinect.dto.request.CreateShowtimeRequest;
import com.cinect.dto.request.UpdateShowtimeRequest;
import com.cinect.dto.response.SeatMapResponse;
import com.cinect.dto.response.SeatResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.entity.*;
import com.cinect.entity.enums.RoomFormat;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ConflictException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;
    private final BookingItemRepository bookingItemRepository;
    private final HoldSeatRepository holdSeatRepository;
    private final ProvinceService provinceService;

    public List<ShowtimeResponse> findFiltered(UUID movieId, UUID cinemaId, String city, Instant startFrom, Instant startTo) {
        return findFiltered(movieId, cinemaId, city, null, startFrom, startTo);
    }

    public List<ShowtimeResponse> findFiltered(UUID movieId, UUID cinemaId, String city, RoomFormat format, Instant startFrom, Instant startTo) {
        var from = startFrom != null ? startFrom : Instant.now();
        var to = startTo != null ? startTo : from.plus(Duration.ofDays(7));
        String provinceCode = cinemaId != null ? null : provinceService.resolveToNewCode(city);
        var list = (format == null)
                ? showtimeRepository.findFilteredNoFormat(movieId, cinemaId, provinceCode, from, to)
                : showtimeRepository.findFiltered(movieId, cinemaId, provinceCode, format, from, to);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ShowtimeResponse> search(UUID movieId, UUID cinemaId, String date, String format, String city) {
        Instant startFrom = null;
        Instant startTo = null;
        if (date != null && !date.isEmpty()) {
            var localDate = java.time.LocalDate.parse(date.strip());
            var zone = java.time.ZoneId.systemDefault();
            startFrom = localDate.atStartOfDay(zone).toInstant();
            startTo = localDate.plusDays(1).atStartOfDay(zone).toInstant();
        }
        RoomFormat roomFormat = null;
        if (format != null && !format.isBlank()) {
            roomFormat = RoomFormat.fromValue(format.strip());
        }
        return findFiltered(movieId, cinemaId, city, roomFormat, startFrom, startTo);
    }

    public ShowtimeResponse findById(UUID id) {
        var st = showtimeRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        return toResponse(st);
    }

    public boolean checkConflicts(UUID roomId, Instant startTime, Instant endTime, UUID excludeShowtimeId) {
        var conflicts = showtimeRepository.findConflicting(roomId, startTime, endTime);
        return conflicts.stream().anyMatch(s -> !s.getId().equals(excludeShowtimeId));
    }

    @Transactional
    public ShowtimeResponse create(CreateShowtimeRequest req) {
        if (req.getStartTime() != null) {
            // Prevent creating historical showtimes from admin UI.
            // Allow small clock skew between client/server.
            Instant minAllowed = Instant.now().minusSeconds(30);
            if (req.getStartTime().isBefore(minAllowed)) {
                throw new BadRequestException("Showtime startTime must be in the future");
            }
        }
        var movie = movieRepository.findById(req.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        var room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        var cinema = cinemaRepository.findById(req.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        if (!room.getCinema().getId().equals(cinema.getId())) {
            throw new BadRequestException("Room does not belong to cinema");
        }
        var duration = movie.getDuration() != null ? movie.getDuration() : 120;
        var endTime = req.getEndTime() != null
                ? req.getEndTime()
                : req.getStartTime().plusSeconds(duration * 60L);
        if (endTime.isBefore(req.getStartTime()) || endTime.equals(req.getStartTime())) {
            endTime = req.getStartTime().plusSeconds(duration * 60L);
        }
        if (checkConflicts(room.getId(), req.getStartTime(), endTime, null)) {
            throw new ConflictException("Room has conflicting showtime");
        }
        var showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .cinema(cinema)
                .startTime(req.getStartTime())
                .endTime(endTime)
                .basePrice(req.getBasePrice())
                .format(req.getFormat())
                .language(req.getLanguage())
                .subtitles(req.getSubtitles())
                .isActive(true)
                .memberExclusive(false)
                .build();
        showtime = showtimeRepository.save(showtime);
        return toResponse(showtime);
    }

    @Transactional
    public ShowtimeResponse update(UUID id, UpdateShowtimeRequest req) {
        var st = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        if (req.getMovieId() != null) {
            var movie = movieRepository.findById(req.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            st.setMovie(movie);
        }
        if (req.getRoomId() != null) {
            var room = roomRepository.findById(req.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
            st.setRoom(room);
        }
        if (req.getCinemaId() != null) {
            var cinema = cinemaRepository.findById(req.getCinemaId()).orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
            st.setCinema(cinema);
        }
        if (req.getStartTime() != null) {
            Instant minAllowed = Instant.now().minusSeconds(30);
            if (req.getStartTime().isBefore(minAllowed)) {
                throw new BadRequestException("Showtime startTime must be in the future");
            }
            st.setStartTime(req.getStartTime());
        }
        if (req.getEndTime() != null) st.setEndTime(req.getEndTime());
        if (req.getBasePrice() != null) st.setBasePrice(req.getBasePrice());
        if (req.getFormat() != null) st.setFormat(req.getFormat());
        if (req.getLanguage() != null) st.setLanguage(req.getLanguage());
        if (req.getSubtitles() != null) st.setSubtitles(req.getSubtitles());
        if (req.getIsActive() != null) st.setIsActive(req.getIsActive());
        if (req.getStartTime() != null && req.getEndTime() == null && st.getMovie() != null) {
            st.setEndTime(req.getStartTime().plusSeconds(st.getMovie().getDuration() * 60L));
        }
        st = showtimeRepository.save(st);
        return toResponse(st);
    }

    @Transactional
    public void delete(UUID id) {
        var st = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        st.setIsActive(false);
        showtimeRepository.save(st);
    }

    public SeatMapResponse getSeatMap(UUID showtimeId) {
        var showtime = showtimeRepository.findDetailById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        var seats = seatRepository.findByRoomId(showtime.getRoom().getId());
        var bookedIds = new HashSet<>(bookingItemRepository.findBookedSeatIds(showtimeId));
        var heldIds = new HashSet<>(holdSeatRepository.findHeldSeatIds(showtimeId, Instant.now()));

        var seatResponses = seats.stream().map(s -> {
            var status = bookedIds.contains(s.getId()) ? com.cinect.entity.enums.SeatStatus.BOOKED
                    : heldIds.contains(s.getId()) ? com.cinect.entity.enums.SeatStatus.BOOKED
                    : com.cinect.entity.enums.SeatStatus.AVAILABLE;
            return SeatResponse.builder()
                    .id(s.getId())
                    .roomId(s.getRoom().getId())
                    .row(s.getRowLabel())
                    .rowLabel(s.getRowLabel())
                    .number(s.getNumber())
                    .type(s.getType())
                    .status(status)
                    .isAisle(s.getIsAisle())
                    .price(s.getPrice())
                    .build();
        }).collect(Collectors.toList());

        return SeatMapResponse.builder()
                .seats(seatResponses)
                .bookedSeatIds(bookedIds)
                .heldSeatIds(heldIds)
                .build();
    }

    private ShowtimeResponse toResponse(Showtime s) {
        return ShowtimeResponse.builder()
                .id(s.getId())
                .movieId(s.getMovie().getId())
                .movieTitle(s.getMovie().getTitle())
                .moviePosterUrl(s.getMovie().getPosterUrl())
                .roomId(s.getRoom().getId())
                .roomName(s.getRoom().getName())
                .cinemaId(s.getCinema().getId())
                .cinemaName(s.getCinema().getName())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .basePrice(s.getBasePrice())
                .format(s.getFormat())
                .language(s.getLanguage())
                .subtitles(s.getSubtitles())
                .isActive(s.getIsActive())
                .memberExclusive(s.getMemberExclusive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
