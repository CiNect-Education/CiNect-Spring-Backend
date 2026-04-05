package com.cinect.config;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WebsocketGateway {

    private final SocketIONamespace namespace;

    public WebsocketGateway(SocketIOServer server) {
        // Lắng nghe trên namespace "/ws" giống hệt NestJS
        this.namespace = server.addNamespace("/ws");

        this.namespace.addEventListener("joinShowtime", ShowtimeRequest.class, (client, data, ackSender) -> {
            String room = "showtimes:" + data.getShowtimeId();
            client.joinRoom(room);
            System.out.println("🔗 Trình duyệt đã tham gia phòng: " + room);
        });

        this.namespace.addEventListener("leaveShowtime", ShowtimeRequest.class, (client, data, ackSender) -> {
            String room = "showtimes:" + data.getShowtimeId();
            client.leaveRoom(room);
            System.out.println("👋 Trình duyệt đã rời phòng: " + room);
        });
    }

    public void broadcastSeatEvent(String type, String showtimeId, List<String> seatIds) {
        String room = "showtimes:" + showtimeId;
        Map<String, Object> payload = Map.of("showtimeId", showtimeId, "seatIds", seatIds);
        namespace.getRoomOperations(room).sendEvent(type, payload);
        System.out.println("📢 Đã phát lệnh " + type + " tới phòng " + room + ". Ghế: " + seatIds);
    }

    @Data
    public static class ShowtimeRequest {
        private String showtimeId;
    }
}