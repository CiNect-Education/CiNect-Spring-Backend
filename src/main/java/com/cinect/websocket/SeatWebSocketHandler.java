package com.cinect.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatWebSocketHandler {

    public static final String SEAT_HELD = "SEAT_HELD";
    public static final String SEAT_RELEASED = "SEAT_RELEASED";
    public static final String SEAT_BOOKED = "SEAT_BOOKED";
    public static final String HOLD_EXPIRED = "HOLD_EXPIRED";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastSeatEvent(UUID showtimeId, String event, List<UUID> seatIds) {
        try {
            var payload = Map.<String, Object>of("event", event, "seatIds", seatIds);
            messagingTemplate.convertAndSend("/topic/showtimes/" + showtimeId, payload);
        } catch (Exception ignored) {
        }
    }

    public void broadcastHoldExpired(UUID showtimeId, List<UUID> seatIds) {
        broadcastSeatEvent(showtimeId, HOLD_EXPIRED, seatIds);
    }
}
