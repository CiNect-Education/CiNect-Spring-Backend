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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoldService {

    private final HoldRepository holdRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final WebsocketGateway websocketGateway;

    @Transactional
    public void holdSeats(String userEmail, HoldRequest request) {
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

        // 2. Phát sự kiện WebSocket để Frontend đổi màu ghế lập tức
        List<String> stringSeatIds = request.getSeatIds().stream().map(Object::toString).collect(Collectors.toList());
        websocketGateway.broadcastSeatEvent("SEAT_HELD", request.getShowtimeId().toString(), stringSeatIds);
    }
}