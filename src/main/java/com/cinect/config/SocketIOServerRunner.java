package com.cinect.config;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketIOServerRunner {

    private final SocketIOServer server;

    @EventListener(ApplicationReadyEvent.class)
    public void startSocketIOServer() {
        server.start();
        System.out.println("===============================================");
        System.out.println("✅ Socket.IO Server STARTED successfully on port 8082");
        System.out.println("===============================================");
    }

    @PreDestroy
    public void stopSocketIOServer() {
        server.stop();
        System.out.println("🛑 Socket.IO Server STOPPED");
    }
}