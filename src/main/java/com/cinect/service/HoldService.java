package com.cinect.service;

import com.cinect.config.WebsocketGateway;
import com.cinect.dto.request.HoldRequest;
import com.cinect.entity.Hold;
import com.cinect.entity.enums.HoldStatus;
import com.cinect.repository.HoldRepository;
import com.cinect.repository.ShowtimeRepository;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HoldService {

    private final HoldRepository holdRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final WebsocketGateway websocketGateway;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Map<String, Object> holdSeats(String userEmail, HoldRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // 1. Tạo bản ghi Hold mới trong DB (Giữ ghế trong 10 phút)
        Hold hold = new Hold();
        hold.setUser(user);
        hold.setShowtime(showtime);
        hold.setStatus(HoldStatus.ACTIVE);
        hold.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
        hold.setSeatIds(request.getSeatIds()); 
        
        holdRepository.save(hold);

        // QUAN TRỌNG: Lưu chi tiết từng ghế vào bảng hold_seats để getSeatMap có thể đọc được!
        for (UUID seatId : request.getSeatIds()) {
            jdbcTemplate.update(
                "INSERT INTO hold_seats (hold_id, showtime_id, seat_id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
                hold.getId(), request.getShowtimeId(), seatId
            );
        }

        // 2. Phát sự kiện WebSocket để Frontend đổi màu ghế lập tức
        List<String> stringSeatIds = request.getSeatIds().stream().map(Object::toString).collect(Collectors.toList());
        websocketGateway.broadcastSeatEvent("SEAT_HELD", request.getShowtimeId().toString(), stringSeatIds);
        Map<String, Object> response = new HashMap<>();
        response.put("holdId", hold.getId());
        response.put("expiresAt", hold.getExpiresAt().toString());
        
        return response;
    }
}